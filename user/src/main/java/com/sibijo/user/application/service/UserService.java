package com.sibijo.user.application.service;

import com.sibijo.common.utils.Auth.AuthUtil;
import com.sibijo.common.utils.Auth.JwtUtil;
import com.sibijo.user.domain.enums.Role;
import com.sibijo.user.domain.model.User;
import com.sibijo.user.domain.repository.UserRepository;
import com.sibijo.user.presentation.dto.SignUpRequestDto;
import com.sibijo.user.presentation.dto.SignUpResponseDto;
import com.sibijo.user.presentation.dto.UserCreateRequestDto;
import com.sibijo.user.presentation.dto.UserCreateResponseDto;
import com.sibijo.user.presentation.dto.UserDetailsResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

        // 세부 권한 처리
        // 특정 역할을 가진 사용자 (허브 관리자, 배송 담당자, 업체 담당자)인 경우 자기 자신만 조회 가능
        Set<String> targetRoles = Set.of(Role.HUB.getAuthority(), Role.DELIVERY.getAuthority(),
                Role.COMPANY.getAuthority());
        authUtil.authorizeSelfAccess(request, id, targetRoles);

        // 중복 체크
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

}
