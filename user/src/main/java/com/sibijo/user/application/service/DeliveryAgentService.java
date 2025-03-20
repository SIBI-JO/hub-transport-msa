package com.sibijo.user.application.service;

import com.sibijo.common.exception.CustomException;
import com.sibijo.common.exception.codes.CommonExceptionCode;
import com.sibijo.common.utils.Auth.AuthUtil;
import com.sibijo.common.utils.page.PageSize;
import com.sibijo.common.utils.page.PageableUtils;
import com.sibijo.user.application.dto.HubResponseDto;
import com.sibijo.user.domain.enums.DeliveryType;
import com.sibijo.user.domain.enums.Role;
import com.sibijo.user.domain.model.DeliveryAgent;
import com.sibijo.user.domain.model.User;
import com.sibijo.user.domain.repository.DeliveryAgentRepository;
import com.sibijo.user.domain.repository.UserRepository;
import com.sibijo.user.infrastructure.client.FeignClientUtil;
import com.sibijo.user.presentation.dto.deliveryAgent.DeliveryAgentCreateRequestDto;
import com.sibijo.user.presentation.dto.deliveryAgent.DeliveryAgentCreateResponseDto;
import com.sibijo.user.presentation.dto.deliveryAgent.DeliveryAgentDeleteResponseDto;
import com.sibijo.user.presentation.dto.deliveryAgent.DeliveryAgentDetailsResponseDto;
import com.sibijo.user.presentation.dto.deliveryAgent.DeliveryAgentPageResponseDto;
import com.sibijo.user.presentation.dto.deliveryAgent.DeliveryAgentSearchDetailsResponseDto;
import com.sibijo.user.presentation.dto.deliveryAgent.DeliveryAgentUpdateRequestDto;
import com.sibijo.user.presentation.dto.deliveryAgent.DeliveryAgentUpdateResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryAgentService {

    private final UserRepository userRepository;
    private final DeliveryAgentRepository deliveryAgentRepository;
    private final AuthUtil authUtil;
    private final FeignClientUtil feignClientUtil;

    @Transactional
    public DeliveryAgentCreateResponseDto createDeliveryAgent(
            DeliveryAgentCreateRequestDto requestDto, HttpServletRequest request) {

        DeliveryType deliveryType = requestDto.getDeliveryType();
        UUID hubId = requestDto.getHubId();

        // 배송담당자 중복 확인
        Long userId = requestDto.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 세부 권한 처리
        // 특정 역할을 가진 사용자 (허브 관리자)인 경우 자기 자신만 조회 가능
        Set<String> targetRoles = Set.of(Role.HUB.getAuthority());
        authUtil.authoizeHubAccess(request, user.getHubId(), targetRoles);

        Optional<DeliveryAgent> existingAgent = deliveryAgentRepository.findByIdForUpdate(userId);
        if (existingAgent.isPresent()) {
            throw new IllegalArgumentException("이미 등록된 배송담당자입니다.");
        }

        int deliveryOrder = 0;

        // 업체 배송 담당자
        if (deliveryType.equals(DeliveryType.COMPANY)) {
            // TODO: 허브 ID 존재 확인 => msa to msa api 요청!
            HubResponseDto response = feignClientUtil.CallHubFeignClient(requestDto.getHubId());
            if (!response.isHubStatus()) {
                throw new IllegalArgumentException("등록이 불가한 허브입니다.");
            }
            // 배송 순번 지정 0~10 - 같은 hubId, deliveryType을 가진 기존 배송 담당자 중 max(deliveryOrder) 찾기
            Optional<Integer> maxOrderOpt = deliveryAgentRepository.findMaxDeliveryOrderByHubIdAndType(
                    hubId, deliveryType);

            if (maxOrderOpt.isPresent()) {
                deliveryOrder = (maxOrderOpt.get() + 1) % 10;
            }
        }
        // 허브 배송 담당자
        else if (deliveryType.equals(DeliveryType.HUB)) {
            // 배송 순번 지정 0~10 - 같은 deliveryType을 가진 기존 배송 담당자 중 max(deliveryOrder) 찾기
            Optional<Integer> maxOrderOpt = deliveryAgentRepository.findMaxDeliveryOrderByType(
                    deliveryType);

            if (maxOrderOpt.isPresent()) {
                deliveryOrder = (maxOrderOpt.get() + 1) % 10;
            }
        }

        // 배송담당자 생성
        DeliveryAgent deliveryAgent = DeliveryAgent.of(user, hubId, deliveryType,
                deliveryOrder);
        deliveryAgentRepository.saveAndFlush(deliveryAgent);

        return DeliveryAgentCreateResponseDto
                .builder()
                .userId(user.getId())
                .hubId(hubId)
                .deliveryType(deliveryType)
                .deliveryOrder(deliveryOrder)
                .build();
    }

    public DeliveryAgentDetailsResponseDto getDeliveryAgent(Long id, HttpServletRequest request) {

        // 존재 확인
        DeliveryAgent user = deliveryAgentRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 사용자입니다.")
        );

        // 세부 권한 처리
        // 특정 역할을 가진 사용자 (허브 관리자)인 경우 자기 자신만 조회 가능
        UUID hubId = user.getHubId();
        Set<String> targetRoles = Set.of(Role.HUB.getAuthority());
        authUtil.authoizeHubAccess(request, hubId, targetRoles);
        // 본인 확인
        targetRoles = Set.of(Role.DELIVERY.getAuthority());
        authUtil.authorizeSelfAccess(request, user.getId(), targetRoles);

        return DeliveryAgentDetailsResponseDto
                .builder()
                .userId(user.getId())
                .hubId(user.getHubId())
                .deliveryType(user.getDeliveryType())
                .deliveryOrder(user.getDeliveryOrder())
                .build();
    }

    @Transactional
    public DeliveryAgentUpdateResponseDto updateDeliveryAgent(Long id,
            DeliveryAgentUpdateRequestDto requestDto, HttpServletRequest request) {

        log.info(requestDto.toString());
        DeliveryAgent user = deliveryAgentRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 사용자입니다.")
        );

        // 세부 권한 처리
        // 특정 역할을 가진 사용자 (허브 관리자)인 경우 자기 자신만 조회 가능
        Set<String> targetRoles = Set.of(Role.HUB.getAuthority());
        authUtil.authoizeHubAccess(request, user.getHubId(), targetRoles);

        DeliveryType deliveryType = requestDto.getDeliveryType();

        // 배송자 타입 변경
        UUID hubId = null;
        if (deliveryType != null) {
            // 허브 -> 업체
            if (deliveryType.equals(DeliveryType.COMPANY)) {
                hubId = requestDto.getHubId();
            }
        }

        // 허브 ID 변경
        if (requestDto.getHubId() != null) {
            if (Objects.equals(deliveryType, DeliveryType.HUB)) {
                throw new IllegalArgumentException("허브 배송 담당자는 허브 정보를 변경할 수 없습니다.");
            }
            //TODO: 허브 ID 존재 확인 (feign client)
            HubResponseDto response = feignClientUtil.CallHubFeignClient(requestDto.getHubId());
            if (!response.isHubStatus()) {
                throw new IllegalArgumentException("존재하지 않는 허브입니다.");
            }

            hubId = requestDto.getHubId();
        }

        //배송 순번 재배치

        int deliveryOrder = user.getDeliveryOrder();
        if (Objects.equals(deliveryType, DeliveryType.HUB) && !user.getHubId().equals(hubId)){
            deliveryOrder = user.getDeliveryOrder();
        }
        else if (Objects.equals(deliveryType, DeliveryType.COMPANY)) {
            // 배송 순번 지정 0~10 - 같은 hubId, deliveryType을 가진 기존 배송 담당자 중 max(deliveryOrder) 찾기
            Optional<Integer> maxOrderOpt = deliveryAgentRepository.findMaxDeliveryOrderByHubIdAndType(
                    hubId, deliveryType);

            if (maxOrderOpt.isPresent()) {
                deliveryOrder = (maxOrderOpt.get() + 1) % 10;
            }
        }
        else if (Objects.equals(deliveryType, DeliveryType.HUB)) {
            Optional<Integer> maxOrderOpt = deliveryAgentRepository.findMaxDeliveryOrderByType(
                    deliveryType);

            if (maxOrderOpt.isPresent()) {
                deliveryOrder = (maxOrderOpt.get() + 1) % 10;
            }
        }

        // 배송담당자 정보 업데이트
        user.update(
                hubId,
                deliveryType,
                deliveryOrder
        );

        // TODO: User hub id도 변경
        // TODO: 변경된 유저 로그아웃 처리

        log.info(user.toString());
        return DeliveryAgentUpdateResponseDto
                .builder()
                .userId(id)
                .hubId(hubId)
                .deliveryType(deliveryType)
                .deliveryOrder(deliveryOrder)
                .build();
    }

    @Transactional
    public DeliveryAgentDeleteResponseDto deleteDeliveryAgent(Long id, HttpServletRequest request) {
        // TODO: 권한 체크

        // 유저 존재 여부 확인
        DeliveryAgent user = deliveryAgentRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 사용자입니다.")
        );

        //이미 삭제된 유저 확인
        if (user.getIsDeleted()) {
            throw new IllegalArgumentException("이미 삭제된 유저입니다.");
        }

        // 세부 권한 처리
        // 특정 역할을 가진 사용자 (허브 관리자)인 경우 자기 자신만 조회 가능
        UUID hubId = user.getHubId();
        Set<String> targetRoles = Set.of(Role.HUB.getAuthority());
        authUtil.authoizeHubAccess(request, hubId, targetRoles);

        //삭제
        user.softDelete();

        return DeliveryAgentDeleteResponseDto
                .builder()
                .userId(user.getId())
                .build();
    }

    public DeliveryAgentPageResponseDto searchDeliveryAgents(HttpServletRequest request, int page, int size,
            String criteria, String sort, String deliveryType) {
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
        Page<DeliveryAgent> userList;
        if (StringUtils.hasText(deliveryType)) {
            userList = deliveryAgentRepository.findAllBydeliveryTypeContains(deliveryType, pageable);
        } else {
            userList = deliveryAgentRepository.findAll(pageable);
        }

        return DeliveryAgentPageResponseDto.builder()
                .page(userList.getNumber() + 1)
                .size(userList.getSize())
                .total(userList.getTotalPages())
                .users(
                        //리스트 형태로 넣기
                        userList.stream()
                                .map(user -> DeliveryAgentSearchDetailsResponseDto.builder()
                                        .userId(user.getId())
                                        .hubId(user.getHubId())
                                        .deliveryType(user.getDeliveryType())
                                        .deliveryOrder(user.getDeliveryOrder())
                                        .isDeleted((user.getDeletedAt() != null))
                                        .build()
                                )
                                .collect(Collectors.toList())
                )
                .build();
    }


}
