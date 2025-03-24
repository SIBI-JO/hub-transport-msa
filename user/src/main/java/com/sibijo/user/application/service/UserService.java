package com.sibijo.user.application.service;

import com.sibijo.common.exception.CustomException;
import com.sibijo.common.exception.codes.CommonExceptionCode;
import com.sibijo.common.utils.Auth.AuthUtil;
import com.sibijo.common.utils.Auth.JwtUtil;
import com.sibijo.common.utils.page.PageSize;
import com.sibijo.common.utils.page.PageableUtils;
import com.sibijo.user.domain.enums.Role;
import com.sibijo.user.domain.model.User;
import com.sibijo.user.domain.repository.UserRepository;
import com.sibijo.user.presentation.dto.user.UserCreateRequestDto;
import com.sibijo.user.presentation.dto.user.UserCreateResponseDto;
import com.sibijo.user.presentation.dto.user.UserDeleteResponseDto;
import com.sibijo.user.presentation.dto.user.UserDetailsResponseDto;
import com.sibijo.user.presentation.dto.user.UserPageResponseDto;
import com.sibijo.user.presentation.dto.user.UserSearchDetailsReponseDto;
import com.sibijo.user.presentation.dto.user.UserUpdateRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthUtil authUtil;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;


    public UserCreateResponseDto createUser(UserCreateRequestDto requestDto) {
        String username = requestDto.getUsername();
        UUID hubId = requestDto.getHubId();
        UUID companyId = requestDto.getCompanyId();
        String slackId = requestDto.getSlackId();

        // 회원 중복 확인
        Optional<User> checkUsername = userRepository.findByUsername(username);
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }

        // slackId 중복확인
        Optional<User> checkEmail = userRepository.findBySlackId(slackId);
        if (checkEmail.isPresent()) {
            throw new IllegalArgumentException("중복된 Email 입니다.");
        }

        // 유저 생성
        String password = passwordEncoder.encode(requestDto.getPassword());
        Role role = requestDto.getRole();

        User user = User.of(username, password, slackId, role, hubId, companyId);
        userRepository.save(user);

        return UserCreateResponseDto
                .builder()
                .userId(user.getId())
                .build();
    }

    public UserDetailsResponseDto getUser(Long id, HttpServletRequest request) {

        // 세부 권한 처리
        // 특정 역할을 가진 사용자 (허브 관리자, 배송 담당자, 업체 담당자)인 경우 자기 자신만 조회 가능
        Set<String> targetRoles = Set.of(Role.HUB.getAuthority(), Role.DELIVERY.getAuthority(),
                Role.COMPANY.getAuthority());
        authUtil.authorizeSelfAccess(request, id, targetRoles);

        // 존재 확인
        User user = userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 사용자입니다.")
        );

        return UserDetailsResponseDto
                .builder()
                .userId(user.getId())
                .username(user.getUsername())
                .slackId(user.getSlackId())
                .build();
    }

    @Transactional
    public UserDetailsResponseDto updateUser(Long id, UserUpdateRequestDto requestDto, HttpServletRequest request) {

        log.info(requestDto.toString());
        User user = userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 사용자입니다.")
        );

        // username, slackId 중복 확인
        if (StringUtils.hasText(requestDto.getUsername()) && !user.getUsername()
                .equals(requestDto.getUsername())) {
            Optional<User> userFindByUsername = userRepository.findByUsername(
                    requestDto.getUsername());
            if (userFindByUsername.isPresent()) {
                throw new IllegalArgumentException("이미 존재하는 username입니다.");
            }
        }

        if (StringUtils.hasText(requestDto.getSlackId()) && !user.getSlackId()
                .equals(requestDto.getSlackId())) {
            Optional<User> userFindBySlackId = userRepository.findBySlackId(
                    requestDto.getSlackId());
            if (userFindBySlackId.isPresent()) {
                throw new IllegalArgumentException("이미 존재하는 slack ID입니다.");
            }
        }

        String newPassword = null;
        // newPassword 가 있으면 originPassword 확인
        if (StringUtils.hasText(requestDto.getNewPassword())) {
            if (!passwordEncoder.matches(requestDto.getOriginPassword(), user.getPassword())) {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }
            newPassword = passwordEncoder.encode(requestDto.getNewPassword());
        }

        // 유저 정보 업데이트
        user.updateUser(
                requestDto.getUsername(),
                requestDto.getSlackId(),
                newPassword
        );

        //blacklist 처리
        boolean usernameChanged = StringUtils.hasText(requestDto.getUsername()) &&
                !user.getUsername().equals(requestDto.getUsername());
        boolean passwordChanged = StringUtils.hasText(requestDto.getNewPassword());
        String targetUserToken = (String) redisTemplate.opsForValue().get("jwt:user:" + id);
        if (targetUserToken != null && (usernameChanged || passwordChanged)) {
            saveBlacklist(targetUserToken); // 수정된 사용자의 token blacklist 처리
            redisTemplate.delete("jwt:user:" + id); // 기존 토큰 삭제
        }

        log.info(user.toString());
        return UserDetailsResponseDto
                .builder()
                .userId(user.getId())
                .username(user.getUsername())
                .slackId(user.getSlackId())
                .build();
    }

    @Transactional
    public UserDeleteResponseDto deleteUser(Long id, HttpServletRequest request) {

        // 유저 존재 여부 확인
        User user = userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 사용자입니다.")
        );

        //이미 삭제된 유저 확인
        if (user.getIsDeleted()) {
            throw new IllegalArgumentException("이미 삭제된 유저입니다.");
        }

        //삭제
        userRepository.deleteById(id);
        //blacklist 처리
        String targetUserToken = (String) redisTemplate.opsForValue().get("jwt:user:" + id);
        if (targetUserToken != null) {
            saveBlacklist(targetUserToken); // 삭제된 사용자의 token blacklist 처리
        }

        return UserDeleteResponseDto
                .builder()
                .userId(user.getId())
                .build();
    }

    public UserPageResponseDto searchUsers(HttpServletRequest request, int page, int size,
            String criteria, String sort, String username) {

        // 유효한 페이지 크기인지 검증
        if (!PageSize.isValidSize(size)) {
            throw new CustomException(CommonExceptionCode.INVALID_PAGE_SIZE);
        }

        // sort 설정
        String pageCriteria = criteria.equals("createdAt") ? "createdAt" : "updatedAt";

        Sort pageSort = sort.equals("ASC") ? Sort.by(Sort.Direction.ASC, pageCriteria)
                : Sort.by(Sort.Direction.DESC, pageCriteria);

        // 페이지네이션 설정
        Pageable pageable = PageableUtils.validatePageable(PageRequest.of(page, size, pageSort));

        // username 포함한 유저 검색
        Page<User> userList;
        if (StringUtils.hasText(username)) {
            userList = userRepository.findAllByUsernameContains(username, pageable);
        } else {
            userList = userRepository.findAll(pageable);
        }

        return UserPageResponseDto.builder()
                .page(userList.getNumber() + 1)
                .size(userList.getSize())
                .total(userList.getTotalPages())
                .users(
                        //리스트 형태로 넣기
                        userList.stream()
                                .map(user -> UserSearchDetailsReponseDto.builder()
                                        .userId(user.getId())
                                        .username(user.getUsername())
                                        .slackId(user.getSlackId())
                                        .role(user.getRole())
                                        .isDeleted((user.getDeletedAt() != null))
                                        .build()
                                )
                                .collect(Collectors.toList())
                )
                .build();
    }


    public UserDetailsResponseDto getUserByHubId(UUID hubId) {
        // 존재 확인
        User user = userRepository.findByHubIdAndRole(hubId, Role.HUB).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 사용자입니다.")
        );

        return UserDetailsResponseDto
                .builder()
                .userId(user.getId())
                .username(user.getUsername())
                .slackId(user.getSlackId())
                .role(user.getRole())
                .build();
    }

    private void saveBlacklist(String token) {
        log.info("blacklist 저장할 token: {}", token);
        try {
            token = token.substring(7);

            Date expiration = jwtUtil.extractExpiration(token);
            long now = System.currentTimeMillis();
            long ttl = expiration.getTime() - now;

            if (ttl > 0) {
                redisTemplate.opsForValue().set(
                        "blacklist:" + token,
                        "logout",
                        ttl,
                        TimeUnit.MILLISECONDS
                );
                log.info("Token blacklisted successfully");
            }
        } catch (Exception e) {
            log.error("블랙리스트 저장 중 예외 발생: {}", e.getMessage(), e);
        }
    }
}
