package com.sibijo.user.application.service;

import com.sibijo.user.application.dto.HubResponseDto;
import com.sibijo.user.domain.enums.Role;
import com.sibijo.user.domain.model.DeliveryAgent;
import com.sibijo.user.domain.model.User;
import com.sibijo.user.domain.repository.DeliveryAgentRepository;
import com.sibijo.user.domain.repository.UserRepository;
import com.sibijo.user.infrastructure.client.FeignClientService;
import com.sibijo.user.presentation.dto.auth.AssignRoleRequestDto;
import com.sibijo.user.presentation.dto.auth.AssignRoleResponseDto;
import com.sibijo.user.presentation.dto.auth.CommonSignUpRequestDto;
import com.sibijo.user.presentation.dto.auth.MasterSignUpRequestDto;
import com.sibijo.user.presentation.dto.user.SignUpResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FeignClientService feignClientService;
    private final DeliveryAgentRepository deliveryAgentRepository;

    @Value("${admin.token}")
    private String ADMIN_TOKEN;

    public SignUpResponseDto signUpMaster(MasterSignUpRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());

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

        // master관리자 권한 Token 대조
        if (!ADMIN_TOKEN.equals(requestDto.getAdminToken())) {
            throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
        }

        User user = User.of(username, password, slackId, Role.MASTER, null, null);
        userRepository.save(user);

        return SignUpResponseDto
                .builder()
                .userId(user.getId())
                .build();
    }

    public SignUpResponseDto signUpCommon(CommonSignUpRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());

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

        User user = User.of(username, password, slackId, null, null, null);
        userRepository.save(user);

        return SignUpResponseDto
                .builder()
                .userId(user.getId())
                .build();
    }


    @Transactional
    public AssignRoleResponseDto assignUserRole(AssignRoleRequestDto requestDto) {

        // username 존재 확인
        User user = userRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));
        log.info("requestDto : {}", requestDto);
        log.info("user : {}", user.getRole());
        // role에 따라 처리
        switch (requestDto.getRole()) {
            case HUB:
                return assignHubRole(user, requestDto);
            case COMPANY:
                return assignCompanyRole(user, requestDto);
            case DELIVERY:
                return assignDeliveryRole(user, requestDto);
            default:
                throw new IllegalArgumentException("유효하지 않은 역할입니다.");
        }
    }

    /**
     * 내부 함수
     */

    private AssignRoleResponseDto assignHubRole(User user, AssignRoleRequestDto requestDto) {
        // feign 호출
        HubResponseDto response = feignClientService.CallHubFeignClient(requestDto.getHubId());
        if (!response.isHubStatus()) {
            throw new IllegalArgumentException("등록이 불가한 허브입니다.");
        }
        // role, hubId 업데이트
        user.updateHubUser(requestDto.getRole(), requestDto.getHubId());

        return new AssignRoleResponseDto(user.getId(), requestDto.getRole(), requestDto.getHubId(),
                null, null, null);
    }

    private AssignRoleResponseDto assignCompanyRole(User user, AssignRoleRequestDto requestDto) {
        // feign 호출
        UUID CompanyHubId = feignClientService.CallCompanyFeignClient(requestDto.getCompanyId());
        if (CompanyHubId == null) {
            throw new IllegalArgumentException("등록이 불가한 업체입니다.");
        }
        // role, hubId, companyId 업데이트
        user.updateCompanyUser(requestDto.getRole(), requestDto.getHubId(),
                requestDto.getCompanyId());

        return new AssignRoleResponseDto(user.getId(), requestDto.getRole(), CompanyHubId,
                requestDto.getCompanyId(), null, null);
    }

    private AssignRoleResponseDto assignDeliveryRole(User user, AssignRoleRequestDto requestDto) {
        // deliveryType 에 따라
        switch (requestDto.getDeliveryType()) {
            case HUB:
                return assignHubDeliveryRole(user, requestDto);
            case COMPANY:
                return assignCompanyDeliveryRole(user, requestDto);
            default:
                throw new IllegalArgumentException("유효하지 않은 타입입니다.");
        }

    }

    private AssignRoleResponseDto assignCompanyDeliveryRole(User user,
            AssignRoleRequestDto requestDto) {
        // 허브 존재 확인
        HubResponseDto response = feignClientService.CallHubFeignClient(requestDto.getHubId());
        if (!response.isHubStatus()) {
            throw new IllegalArgumentException("등록이 불가한 허브입니다.");
        }
        // TODO: 배송담당자 생성 & user 정보 update 중 하나가 실패하면 rollback 처리하는 로직 필요
        // user role, hub 정보 update
        user.updateDeliveryUser(requestDto.getRole(), requestDto.getHubId());

        // 배송담당자 생성
        // TODO: 이미 생성된 배송담당자인지 확인 => upsert
        // 배송 순번 지정 0~10 - 같은 hubId, deliveryType을 가진 기존 배송 담당자 중 max(deliveryOrder) 찾기
        int deliveryOrder = 0;
        Optional<Integer> maxOrderOpt = deliveryAgentRepository.findMaxDeliveryOrderByHubIdAndType(
                requestDto.getHubId(), requestDto.getDeliveryType());

        if (maxOrderOpt.isPresent()) {
            deliveryOrder = (maxOrderOpt.get() + 1) % 10;
        }
        // 생성
        DeliveryAgent deliveryAgent = DeliveryAgent.of(user, requestDto.getHubId(),
                requestDto.getDeliveryType(),
                deliveryOrder);
        deliveryAgentRepository.saveAndFlush(deliveryAgent);

        return new AssignRoleResponseDto(user.getId(), Role.DELIVERY, requestDto.getHubId(),
                null, requestDto.getDeliveryType(), deliveryOrder);

    }

    private AssignRoleResponseDto assignHubDeliveryRole(User user,
            AssignRoleRequestDto requestDto) {

        // TODO: 배송담당자 생성 & user 정보 update 중 하나가 실패하면 rollback 처리하는 로직 필요
        // user role, hub 정보 update
        user.updateDeliveryUser(requestDto.getRole(), requestDto.getHubId());

        // 배송담당자 생성
        // TODO: 이미 생성된 배송담당자인지 확인 => upsert
        // 배송 순번 지정 0~10 - 같은 hubId, deliveryType을 가진 기존 배송 담당자 중 max(deliveryOrder) 찾기
        int deliveryOrder = 0;
        Optional<Integer> maxOrderOpt = deliveryAgentRepository.findMaxDeliveryOrderByType(
                requestDto.getDeliveryType());

        if (maxOrderOpt.isPresent()) {
            deliveryOrder = (maxOrderOpt.get() + 1) % 10;
        }
        // 생성
        DeliveryAgent deliveryAgent = DeliveryAgent.of(user, requestDto.getHubId(),
                requestDto.getDeliveryType(),
                deliveryOrder);
        deliveryAgentRepository.saveAndFlush(deliveryAgent);

        return new AssignRoleResponseDto(user.getId(), Role.DELIVERY, null,
                null, requestDto.getDeliveryType(), deliveryOrder);

    }

}
