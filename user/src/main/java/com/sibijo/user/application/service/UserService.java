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
import com.sibijo.user.infrastructure.security.UserDetailsImpl;
import com.sibijo.user.presentation.dto.SignUpRequestDto;
import com.sibijo.user.presentation.dto.SignUpResponseDto;
import com.sibijo.user.presentation.dto.UserCreateRequestDto;
import com.sibijo.user.presentation.dto.UserCreateResponseDto;
import com.sibijo.user.presentation.dto.UserDeleteResponseDto;
import com.sibijo.user.presentation.dto.UserDetailsResponseDto;
import com.sibijo.user.presentation.dto.UserPageResponseDto;
import com.sibijo.user.presentation.dto.UserSearchDetailsReponseDto;
import com.sibijo.user.presentation.dto.UserUpdateRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthUtil authUtil;

    @Value("${admin.token}")
    private String ADMIN_TOKEN;

    public SignUpResponseDto signup(SignUpRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());
        String hubId = requestDto.getHubId();
        String companyId = requestDto.getCompanyId();

        // 회원 중복 확인
        Optional<User> checkUsername = userRepository.findByUsername(username);
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }

        // slackId 중복확인
        String slackId = requestDto.getSlackId();
        Optional<User> checkEmail = userRepository.findBySlackId(slackId);
        if (checkEmail.isPresent()) {
            throw new IllegalArgumentException("중복된 Email 입니다.");
        }

        // 사용자 ROLE 확인 ( master관리자 권한은 Token으로 확인 )
        Role role = requestDto.getRole();
        if (role.equals(Role.MASTER)) { //관리자 권한
            System.out.print(ADMIN_TOKEN);
            System.out.print(requestDto.getAdminToken());
            if (!ADMIN_TOKEN.equals(requestDto.getAdminToken())) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
        }

        /*
        사용자 등록

        MASTER - hubId, companyId 없음
        HUB - hubId 필수, companyId 없음
        COMPANY - hubId, companyId 필수
        DELIVERY - hubId 필수, companyId 없음
        */

        User user = User.of(username, password, slackId, role, hubId, companyId);
        userRepository.save(user);

        return SignUpResponseDto
                .builder()
                .userId(user.getId())
                .build();
    }

    public UserCreateResponseDto createUser(UserCreateRequestDto requestDto, HttpServletRequest request) {
        String username = requestDto.getUsername();
        String hubId = requestDto.getHubId();
        String companyId = requestDto.getCompanyId();
        String slackId = requestDto.getSlackId();

        // TODO: 권한 체크 ( Header에서 가져온 값 기반으로 Role, 본인 여부 판단)

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

        // TODO: 권한 체크

        log.info(requestDto.toString());
        User user = userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 사용자입니다.")
        );

        //TODO: username 변경 시, Jwt 를 새로 발급해 주던지, 로그아웃 시키고, 로그인하도록 해야함. => 지금은 client가 없으므로, Jwt를 새로 발급해줘야 할 듯.

        // username, slackId 중복 확인
        if (StringUtils.hasText(requestDto.getUsername()) && !user.getUsername().equals(requestDto.getUsername())) {
            Optional<User> userFindByUsername = userRepository.findByUsername(requestDto.getUsername());
            if (userFindByUsername.isPresent()) {
                throw new IllegalArgumentException("이미 존재하는 username입니다.");
            }
        }

        if (StringUtils.hasText(requestDto.getSlackId()) && !user.getSlackId().equals(requestDto.getSlackId())) {
            Optional<User> userFindBySlackId = userRepository.findBySlackId(requestDto.getSlackId());
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

        log.info(user.toString());
        return UserDetailsResponseDto
                .builder()
                .userId(user.getId())
                .username(user.getUsername())
                .slackId(user.getSlackId())
                .build();
    }

    public UserDeleteResponseDto deleteUser(Long id, HttpServletRequest request) {
        // TODO: 권한 체크

        // 유저 존재 여부 확인
        User user = userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 사용자입니다.")
        );

        //이미 삭제된 유저 확인
        if(user.getIsDeleted()){
            throw new IllegalArgumentException("이미 삭제된 유저입니다.");
        }

        //삭제
        userRepository.deleteById(id);

        return UserDeleteResponseDto
                .builder()
                .userId(user.getId())
                .build();
    }

    public UserPageResponseDto searchUsers(HttpServletRequest request, int page, int size, String criteria, String sort, String username) {
        // 유효한 페이지 크기인지 검증
        if (!PageSize.isValidSize(size)) {
            throw new CustomException(CommonExceptionCode.INVALID_PAGE_SIZE);
        }

        // sort 설정
        String pageCriteria  = criteria.equals("createdAt") ? "createdAt" : "updatedAt";

        Sort pageSort = sort.equals("ASC") ? Sort.by(Sort.Direction.ASC, pageCriteria)  : Sort.by(Sort.Direction.DESC, pageCriteria);

        // 페이지네이션 설정
        Pageable pageable = PageableUtils.validatePageable(PageRequest.of(page, size, pageSort));

        // username 포함한 유저 검색
        Page<User> userList;
        if (StringUtils.hasText(username)) {
            userList = userRepository.findAllByUsernameContains(username, pageable);
        }
        else {
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

}
