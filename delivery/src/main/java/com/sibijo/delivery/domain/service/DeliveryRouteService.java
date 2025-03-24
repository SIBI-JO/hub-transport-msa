package com.sibijo.delivery.domain.service;

import com.sibijo.common.exception.CustomException;
import com.sibijo.common.exception.codes.CommonExceptionCode;
import com.sibijo.common.utils.Auth.JwtUtil;
import com.sibijo.delivery.application.dto.DeliveryResponseDto;
import com.sibijo.delivery.application.dto.DeliveryRouteResponseDto;
import com.sibijo.delivery.domain.entity.Delivery;
import com.sibijo.delivery.domain.entity.DeliveryRoute;
import com.sibijo.delivery.domain.enums.DeliveryDomainExceptionCode;
import com.sibijo.delivery.domain.enums.DeliveryStatusEnum;
import com.sibijo.delivery.domain.repository.DeliveryRouteRepository;
import com.sibijo.delivery.infrastructure.client.user.UserClient;
import com.sibijo.delivery.presentation.dto.DeliveryRouteRequestDto;
import com.sibijo.delivery.presentation.dto.DeliveryRouteUpdateRequestDto;
import com.sibijo.delivery.presentation.dto.DeliveryUpdateRequestDto;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j(topic = "배송경로 Service")
@Service
@RequiredArgsConstructor
public class DeliveryRouteService {

    private final JwtUtil jwtUtil;
    private final DeliveryRouteRepository routeRepository;
    private final UserClient userClient;

    /**
     *  배송 경로 생성
     *  권한 : ALL
     */
    @Transactional
    public DeliveryRoute createDeliveryRoute(DeliveryRouteRequestDto requestDto, Delivery delivery) {

        DeliveryRoute deliveryRoute = DeliveryRoute.createRoute(delivery, requestDto);
        routeRepository.save(deliveryRoute);

        return deliveryRoute;
    }

    /**
     *  배송 경로 전체 조회
     *  권한 : Hub_Manager -> 자신의 허브만    //   Delivery_Manager -> 자신의 배송만
     *                                      // Company_Manager -> 자신의 업체만
     */
    @Transactional(readOnly = true)
    public Page<DeliveryRoute> getDeliveryRoutes(String token, UUID recipientsId, Long deliveryManagerId, Pageable pageable) {

        String role = jwtUtil.extractRole(token);
        Long userId = jwtUtil.extractUserID(token);
        UUID hubId = jwtUtil.extractHubIdForOrder(token);
        UUID companyId = jwtUtil.extractCompanyIdForOrder(token);

        Page<DeliveryRoute> routeList = switch (role) {
            case "MASTER" -> routeRepository.searchDeliveryRoutes(recipientsId, deliveryManagerId, pageable);
            case "HUB" -> routeRepository.searchDeliveryRoutesByHub(hubId, recipientsId, deliveryManagerId, pageable);
            case "DELIVERY" -> routeRepository.searchDeliveryRoutesByDeliveryManager(userId, recipientsId, pageable);
            case "COMPANY" -> routeRepository.searchDeliveryRoutesByCompany(companyId, deliveryManagerId, pageable);
            default -> throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
        };

        return routeList;
    }



    /**
     *  배송 경로 상세 조회
     *  권한 : Hub_Manager -> 자신의 허브만    // Delivery_Manager -> 자신의 배송만
     *                                      // Company_Manager -> 자신의 업체만
     */
    @Transactional(readOnly = true)
    public DeliveryRouteResponseDto getDeliveryRouteDetails(UUID routeId, String token) {
        String role = jwtUtil.extractRole(token);
        Long userId = jwtUtil.extractUserID(token);
        UUID hubId = jwtUtil.extractHubIdForOrder(token);
        UUID companyId = jwtUtil.extractCompanyIdForOrder(token);

        DeliveryRoute route = routeRepository.findById(routeId)
                .filter(o -> o.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "배송 경로가 삭제되었거나 없습니다."));

        switch (role) {
            case "HUB":
                if (!hubId.equals(route.getStartHubId()) && !hubId.equals(route.getEndHubId())) {
                    // 허브 담당자인데 공급업체나 수령업체의 허브 담당자가 아닐 때
                    throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
                }
                break;
            case "DELIVERY":
                if (!Objects.equals(userId, route.getDeliveryManagerId())) {
                    // 배송담당자 -> 만약 업체 배송 담당자라면 -> 본인이 담당 업체 배송 담당자인지 확인
                    throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
                }
                break;
            case "COMPANY":
                if (!companyId.equals(route.getRecipientsId())) {
                    // 업체 담당자인데 수령업체가 자신의 업체가 아닐 때
                    throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
                }
        }
        return new DeliveryRouteResponseDto(route);
    }


    /**
     *  배송 경로 수정
     *  권한 : Hub_Manager -> 자신의 허브만    //   Delivery_Manager -> 자신의 배송만
     *  실제 거리 / 실제 소요 시간만 수정 가능? 아니면 다른 부분도 수정 가눙?
     */
    @Transactional
    public DeliveryRouteResponseDto updateDeliveryRoute(UUID routeId, DeliveryRouteUpdateRequestDto requestDto, Delivery delivery, String token) {
        // JWT에서 Role 추출
        String role = jwtUtil.extractRole(token);
        Long userId = jwtUtil.extractUserID(token);
        UUID hubId = jwtUtil.extractHubIdForOrder(token);

        if (role.equals("COMPANY")) {
            throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
        }

        DeliveryRoute route = routeRepository.findById(routeId)
                .filter(o -> o.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "배송 경로가 삭제되었거나 없습니다."));

        // 허브 담당자인데 공급업체나 수령업체의 허브 담당자가 아닐 때
        switch (role) {
            case "HUB":
                if (!hubId.equals(route.getStartHubId()) && !hubId.equals(route.getEndHubId())) {
                    // 허브 담당자인데 공급업체나 수령업체의 허브 담당자가 아닐 때
                    throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
                }
                break;
            case "DELIVERY":
                if (!Objects.equals(userId, route.getDeliveryManagerId())) {
                    // 배송담당자 -> 만약 업체 배송 담당자라면 -> 본인이 담당 업체 배송 담당자인지 확인
                    throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
                }
        }

        route.updateRoute(requestDto, delivery);
        DeliveryRoute updateRoute = routeRepository.save(route);
        return new DeliveryRouteResponseDto(updateRoute);
    }


    /**
     *  배송 경로 삭제
     *  권한 : Hub_Manager -> 자신의 허브만
     */
    @Transactional
    public DeliveryRouteResponseDto deleteDeliveryRoute(UUID routeId, String token) {

        String role = jwtUtil.extractRole(token);
        UUID hubId = jwtUtil.extractHubIdForOrder(token);

        if (!role.equals("HUB") && !role.equals("MASTER")) {
            throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
        }

        DeliveryRoute route = routeRepository.findById(routeId)
                .filter(o -> o.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "배송 경로가 삭제되었거나 없습니다."));

        // 허브 담당자인데 공급업체나 수령업체의 허브 담당자가 아닐 때
        if (role.equals("HUB") && (!hubId.equals(route.getStartHubId()) && !hubId.equals(route.getEndHubId()))) {
            throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
        }

        routeRepository.deleteById(routeId);
        return new DeliveryRouteResponseDto(route);
    }


    /**
     *  배송 상태 및 배송 담당자 수정
     */
    @Transactional
    public void updateDeliveryStatus(DeliveryRoute route) {
        DeliveryStatusEnum currentStatus = route.getDeliveryStatus();

        switch (currentStatus) {
            case HUB_WAITING -> {
                route.updateDeliveryStatus(DeliveryStatusEnum.HUB_MOVING);
            }
            case HUB_MOVING -> {
                route.updateDeliveryStatus(DeliveryStatusEnum.HUB_ARRIVED);
                // 업체 배송 담당자 ID 요청
                Long companyDeliveryManagerId = userClient.getCompanyDeliveryAgent(route.getEndHubId()).getData();
                route.updateDeliveryManager(companyDeliveryManagerId);
            }
            case HUB_ARRIVED -> {
                route.updateDeliveryStatus(DeliveryStatusEnum.COMPANY_DELIVERING);
            }
            case COMPANY_DELIVERING -> {
                route.updateDeliveryStatus(DeliveryStatusEnum.COMPLETED);
            }
            default -> throw new CustomException(DeliveryDomainExceptionCode.INVALID_DELIVERY_STATUS);
        }
    }

    public DeliveryRoute getDeliveryRouteByDeliveryId(UUID deliveryId) {
        return routeRepository.findByDelivery_DeliveryId(deliveryId);
    }
}
