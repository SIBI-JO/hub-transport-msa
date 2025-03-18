package com.sibijo.user.application.service;

import com.sibijo.common.utils.Auth.AuthUtil;
import com.sibijo.common.utils.Auth.JwtUtil;
import com.sibijo.user.domain.model.User;
import com.sibijo.user.domain.enums.Role;
import com.sibijo.user.domain.repository.UserRepository;
import com.sibijo.user.presentation.dto.UserCreateRequestDto;
import com.sibijo.user.presentation.dto.UserCreateResponseDto;
import com.sibijo.user.presentation.dto.SignUpRequestDto;
import com.sibijo.user.presentation.dto.SignUpResponseDto;
import com.sibijo.user.presentation.dto.UserDetailsResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public UserCreateResponseDto createUser(UserCreateRequestDto requestDto) {

        // TODO: 권한 체크 ( Header에서 가져온 값 기반으로 Role, 본인 여부 판단)

        // 유저 생성
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());
        String hubId = requestDto.getHubId();
        String companyId = requestDto.getCompanyId();
        String slackId = requestDto.getSlackId();
        Role role = requestDto.getRole();

        User user = User.of(username, password, slackId, role, hubId, companyId);
        userRepository.save(user);

        return UserCreateResponseDto
                .builder()
                .userId(user.getId())
                .build();
    }

    public UserDetailsResponseDto getUser(Long id, HttpServletRequest request) {

        // TODO: 세부 권한 처리
        log.info("User Service: Header: {}", request.getHeader("Authorization"));
        String token = jwtUtil.extractToken(request);
        String role = jwtUtil.extractRole(token);
        String userName = jwtUtil.extractUserName(token);

        if (role == null || userName == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "토큰에서 사용자 정보를 추출할 수 없습니다.");
        }



        User user = userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 사용자입니다.")
        );

        // 특정 역할을 가진 사용자 (허브 관리자, 배송 담당자, 업체 담당자)인 경우 자기 자신만 조회 가능
        log.info("user id, role: {}, {}", userName, role);
        authUtil.checkSelf(userName, user.getUsername(), role, Set.of("HUB", "DELIVERY", "COMPANY"));

        return UserDetailsResponseDto
                .builder()
                .userId(user.getId())
                .username(user.getUsername())
                .slackId(user.getSlackId())
                .build();
    }



//    @Transactional
//    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER') or #id == #userDetails.user.id") // 자기 자신 or MANAGER, MASTER 만 접근 가능
//    public UserDetailsResponseDto updateUser(Long id, UserUpdateRequestDto requestDto, UserDetailsImpl userDetails) {
//
//        log.info(requestDto.toString());
//        User user = userRepository.findById(id).orElseThrow(
//                () -> new IllegalArgumentException("존재하지 않는 사용자입니다.")
//        );
//
//        String currentUsername = userDetails.getUsername();
//        Role currentUserRole = userDetails.getUser().getRole();
//
//        //상위 권한자의 정보 변경 차단(ex. MANAGER가 MASTER 정보는 변경할 수 없음)
//        if(Role.isGreaterThen(user.getRole(), currentUserRole)) {
//            throw new AccessDeniedException("접근 권한이 없는 사용자입니다.");
//        }
//
//        //TODO: username 변경 시, Jwt 를 새로 발급해 주던지, 로그아웃 시키고, 로그인하도록 해야함. => 지금은 client가 없으므로, Jwt를 새로 발급해줘야 할 듯.
//
//        // username, email 중복 확인
//        if (StringUtils.hasText(requestDto.getUsername()) && !user.getUsername().equals(requestDto.getUsername())) {
//            Optional<User> userFindByUsername = userRepository.findByUsername(requestDto.getUsername());
//            if (userFindByUsername.isPresent()) {
//                throw new IllegalArgumentException("이미 존재하는 username입니다.");
//            }
//        }
//
//        if (StringUtils.hasText(requestDto.getEmail()) && !user.getEmail().equals(requestDto.getEmail())) {
//            Optional<User> userFindByEmail = userRepository.findByEmail(requestDto.getEmail());
//            if (userFindByEmail.isPresent()) {
//                throw new IllegalArgumentException("이미 존재하는 email입니다.");
//            }
//        }
//
//        String newPassword = null;
//        // newPassword 가 있으면 originPassword 확인
//        if (StringUtils.hasText(requestDto.getNewPassword())) {
//            if (!passwordEncoder.matches(requestDto.getOriginPassword(), user.getPassword())) {
//                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
//            }
//            newPassword = passwordEncoder.encode(requestDto.getNewPassword());
//        }
//
//        // 유저 정보 업데이트
//        user.updateUser(
//                requestDto.getUsername(),
//                requestDto.getEmail(),
//                newPassword
//        );
//
//        log.info(user.toString());
//        return UserDetailsResponseDto
//                .builder()
//                .userId(user.getId())
//                .username(user.getUsername())
//                .email(user.getEmail())
//                .build();
//    }
//
//    @Transactional
//    @PreAuthorize("hasRole('MASTER') or #id == #userDetails.user.id") // 자기 자신 or MASTER 만 접근 가능
//    public UserDeleteResponseDto deleteUser(Long id, UserDetailsImpl userDetails) {
//        // 유저 존재 여부 확인
//        User user = userRepository.findById(id).orElseThrow(
//                () -> new IllegalArgumentException("존재하지 않는 사용자입니다.")
//        );
//
//        //이미 삭제된 유저 확인
//        if(user.getDeletedAt() != null && StringUtils.hasText(String.valueOf(user.getDeletedAt()))){
//            throw new IllegalArgumentException("이미 삭제된 유저입니다.");
//        }
//
//        //삭제
//        String currentUsername = userDetails.getUsername();
//        user.delete(currentUsername); // 삭제한 사람: 로그인한 사용자
//
//        return UserDeleteResponseDto
//                .builder()
//                .userId(user.getId())
//                .build();
//    }
//
//    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER')") //관리자 권한 확인(MANAGER, MASTER)
//    public UserPageResponseDto searchUsers(UserDetailsImpl userDetails, int page, int size, String criteria, String sort, String username) {
//        // sort 설정
//        String pageCriteria  = criteria.equals("createdAt") ? "createdAt" : "updatedAt";
//
//        Sort pageSort = sort.equals("ASC") ? Sort.by(Sort.Direction.ASC, pageCriteria)  : Sort.by(Sort.Direction.DESC, pageCriteria);
//
//        // 페이지네이션 설정
//        Pageable pageable = PageRequest.of(page, size, pageSort);
//
//        // username 포함한 유저 검색
//        Page<User> userList;
//        if (StringUtils.hasText(username)) {
//            userList = userRepository.findAllByUsernameContains(username, pageable);
//        }
//        else {
//            userList = userRepository.findAll(pageable);
//        }
//
//        return UserPageResponseDto.builder()
//                .page(userList.getNumber() + 1)
//                .size(userList.getSize())
//                .total(userList.getTotalPages())
//                .users(
//                        //리스트 형태로 넣기
//                        userList.stream()
//                                .map(user -> UserResponseDto.builder()
//                                        .userId(user.getId())
//                                        .username(user.getUsername())
//                                        .email(user.getEmail())
//                                        .role(user.getRole())
//                                        .deleted((user.getDeletedAt() != null))
//                                        .build()
//                                )
//                                .collect(Collectors.toList())
//                )
//                .build();
//    }


}
