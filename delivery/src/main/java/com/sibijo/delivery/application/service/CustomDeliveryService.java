package com.sibijo.delivery.application.service;

import com.sibijo.common.exception.CustomException;
import com.sibijo.common.exception.codes.CommonExceptionCode;
import com.sibijo.common.utils.Auth.JwtUtil;
import com.sibijo.common.utils.page.PageableUtils;
import com.sibijo.delivery.application.dto.DeliveryResponseDto;
import com.sibijo.delivery.application.dto.DeliveryRouteResponseDto;
import com.sibijo.delivery.domain.entity.Delivery;
import com.sibijo.delivery.domain.entity.DeliveryRoute;
import com.sibijo.delivery.domain.service.DeliveryRouteService;
import com.sibijo.delivery.domain.service.DeliveryService;
import com.sibijo.delivery.infrastructure.client.company.CompanyClient;
import com.sibijo.delivery.infrastructure.client.company.CompanyResponseDto;
import com.sibijo.delivery.infrastructure.client.hub.HubClient;
import com.sibijo.delivery.infrastructure.client.hub.HubResponseDto;
import com.sibijo.delivery.infrastructure.client.order.OrderClient;
import com.sibijo.delivery.infrastructure.client.order.OrderCreateUpdateRequestDto;
import com.sibijo.delivery.infrastructure.client.user.UserClient;
import com.sibijo.delivery.infrastructure.client.user.UserResponseDto;
import com.sibijo.delivery.presentation.dto.DeliveryRequestDto;
import com.sibijo.delivery.presentation.dto.DeliveryRouteRequestDto;
import com.sibijo.delivery.presentation.dto.DeliveryRouteUpdateRequestDto;
import com.sibijo.delivery.presentation.dto.DeliveryUpdateRequestDto;
import com.sibijo.delivery.presentation.dto.OrderToDeliveryRequestDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j(topic = "배송 통합 Service")
@Service
@RequiredArgsConstructor
public class CustomDeliveryService {

    private final JwtUtil jwtUtil;
    private final DeliveryService deliveryService;
    private final DeliveryRouteService deliveryRouteService;
    private final CompanyClient companyClient;
    private final HubClient hubClient;
    private final OrderClient orderClient;
    private final UserClient userClient;

    /**
     *  배송 & 배송 경로 생성
     *  미구현 : User 서버에서 delivery_manager_id 가져와서 넣기
     */

    public void createDelivery(OrderToDeliveryRequestDto requestDto) {

        CompanyResponseDto startHub = null;
        CompanyResponseDto endHub = null;
        HubResponseDto hubRoute = null;
        Long deliveryManagerId = null;


        try {
            // 1. 공급업체 & 수령업체 정보를 통해 출발/도착 허브 조회
            startHub = companyClient.getCompanyOrderInfo(requestDto.getSupplierId()).getData();
            endHub = companyClient.getCompanyOrderInfo(requestDto.getRecipientsId()).getData();
        } catch (Exception e) {
            System.out.println("업체 조회 에러");
        }

//            CompanyResponseDto startHub = new CompanyResponseDto(UUID.fromString("45c4d201-1655-4716-8a7d-66b1d31a0684"), "서울특별시 광진구 12번지");
//            CompanyResponseDto endHub = new CompanyResponseDto(UUID.fromString("46c4d201-1655-4716-8a7d-66b1d31a0685"), "서울특별시 광진구 29번지");
        try {
            // 2. 허브 서버에서 허브 간 경로 조회 (시작 허브와 도착허브가 같으면? )
            hubRoute = hubClient.getHubRouteForOrder(startHub.getHubId(), endHub.getHubId());
//            hubRoute = new HubResponseDto("400km", "4시간 50분");
            System.out.println("허브간 배송 경로 거리:  "+hubRoute.getDistance());
        } catch (Exception e) {
            System.out.println("배송 에러");
        }


        try {
            // 2.5 배송 담당자 정보 가져오기 (시작 허브와 도착허브가 같으면? )
            deliveryManagerId = userClient.getDeliveryAgent().getData();
//            deliveryManagerId = 2L;
        } catch (Exception e) {
//            orderClient.deleteOrderInternal(requestDto.getOrderId());
            System.out.println("배송 에러");
        }




        // 3. 배송 생성에 필요한 정보 생성
        DeliveryRequestDto deliveryRequestDto = new DeliveryRequestDto(
                startHub.getHubId(),
                endHub.getHubId(),
                endHub.getDeliveryAddress(),
                requestDto.getReceiver(),
                requestDto.getReceiverSlackId(),
                requestDto.getRecipientsId(),
                deliveryManagerId
        );

        // 4. 배송 정보 생성 및 저장
        Delivery delivery = deliveryService.createDelivery(deliveryRequestDto);

        // 5. 주문 서버로 배송 ID 보내기
        OrderCreateUpdateRequestDto updateRequestDto = new OrderCreateUpdateRequestDto(
                delivery.getDeliveryId(),
                startHub.getHubId(),
                endHub.getHubId()
        );

        System.out.println("주문의 Id  :   " + requestDto.getOrderId());
        orderClient.updateOrderWithDelivery(requestDto.getOrderId(), updateRequestDto);

        // 5. 배송 경로 생성에 필요한 정보 생성
        DeliveryRouteRequestDto routeRequestDto = new DeliveryRouteRequestDto(
                1,
                startHub.getHubId(),
                endHub.getHubId(),
                requestDto.getRecipientsId(),
                hubRoute.getDistance(),
                hubRoute.getEstimatedTime(),
                deliveryManagerId
        );

        // 6. 배송 경로 생성
        DeliveryRoute deliveryRoute = deliveryRouteService.createDeliveryRoute(routeRequestDto, delivery);

    }


    /**
     *  배송 전체 조회
     *  권한 : Hub_Manager -> 자신의 허브만    //   Delivery_Manager -> 자신의 배송만
     *                                      // Company_Manager -> 자신의 업체만
     */
    public Page<DeliveryResponseDto> getDeliveries(String token, Pageable pageable) {

        Pageable validatedPageable = PageableUtils.validatePageable(pageable);
        Page<Delivery> deliveryList = deliveryService.getDeliveries(token, validatedPageable);
        return deliveryList.map(DeliveryResponseDto::new);
    }



    /**
     *  배송 상세 조회
     *  권한 : Hub_Manager -> 자신의 허브만    //   Delivery_Manager -> 자신의 배송만
     *                                      // Company_Manager -> 자신의 업체만 : 수령인과 SlackId로 본인 확인
     */
    public DeliveryResponseDto getDeliveryDetails(UUID deliveryId, String token) {
        String role = jwtUtil.extractRole(token);
        Long userId = jwtUtil.extractUserID(token);

        // 권한 및 사용자 ID 검증
        if (userId == null || role == null) {
            throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
        }

        return deliveryService.getDeliveryDetails(deliveryId, token);
    }


    /**
     *  배송 수정
     *  권한 : Hub_Manager -> 자신의 허브만    //   Delivery_Manager -> 자신의 배송만
     *  업체 배송 담당자 수정을 어떻게 처리해야하는가
     *  -> 배송을 만들 때 유저 서버로 업체ID를 보내서 업체 관계자 정보 받아서 넣어야 하나?
     */
    public DeliveryResponseDto updateDelivery(UUID deliveryId, DeliveryUpdateRequestDto requestDto, String token) {
        String role = jwtUtil.extractRole(token);
        Long userId = jwtUtil.extractUserID(token);

        System.out.println(role);

        if (role.equals("COMPANY")) {
            throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
        }
        return deliveryService.updateDelivery(deliveryId, requestDto, token);
    }



    /**
     *  배송 취소
     *  권한 : Hub_Manager -> 자신의 허브만
     */
    public DeliveryResponseDto deleteDelivery(UUID deliveryId, String token) {
        String role = jwtUtil.extractRole(token);
        Long userId = jwtUtil.extractUserID(token);

        if (role.equals("COMPANY") || role.equals("DELIVERY")) {
            throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
        }
        return deliveryService.deleteDelivery(deliveryId, token);
    }



    /*******************************************************************
     *  배송 경로 전체 조회
     *  권한 : Hub_Manager -> 자신의 허브만    //   Delivery_Manager -> 자신의 배송만
     *                                      // Company_Manager -> 자신의 업체만
     */
    public Page<DeliveryRouteResponseDto> getDeliveryRoutes(String token, Pageable pageable) {
        Pageable validatedPageable = PageableUtils.validatePageable(pageable);

        String role = jwtUtil.extractRole(token);
        Long userId = jwtUtil.extractUserID(token);

        // 권한 및 사용자 ID 검증
        if (userId == null || role == null) {
            throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
        }

        Page<DeliveryRoute> deliveryRouteList = deliveryRouteService.getDeliveryRoutes(token, validatedPageable);

        return deliveryRouteList.map(DeliveryRouteResponseDto::new);
    }



    /**
     *  배송 경로 상세 조회
     *  권한 : Hub_Manager -> 자신의 허브만    // Delivery_Manager -> 자신의 배송만
     *                                      // Company_Manager -> 자신의 업체만
     */
    public DeliveryRouteResponseDto getDeliveryRouteDetails(UUID routeId, String token) {
        String role = jwtUtil.extractRole(token);
        Long userId = jwtUtil.extractUserID(token);

        // 권한 및 사용자 ID 검증
        if (userId == null || role == null) {
            throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
        }

        return deliveryRouteService.getDeliveryRouteDetails(routeId, token);
    }

    /**
     *  배송 경로 수정
     *  권한 : Hub_Manager -> 자신의 허브만    //   Delivery_Manager -> 자신의 배송만
     *  실제 거리 / 실제 소요 시간만 수정 가능? 아니면 다른 부분도 수정 가눙?
     */
    public DeliveryRouteResponseDto updateDeliveryRoute(UUID routeId, DeliveryRouteUpdateRequestDto requestDto, String token) {
        Delivery delivery = deliveryService.getDeliveryDetailsForUpdate(requestDto.getDeliveryId());
        return deliveryRouteService.updateDeliveryRoute(routeId, requestDto, delivery, token);
    }

    /**
     *  배송 경로 삭제
     *  권한 : Hub_Manager -> 자신의 허브만
     */
    public DeliveryRouteResponseDto deleteDeliveryRoute(UUID routeId, String token) {
        String role = jwtUtil.extractRole(token);
        Long userId = jwtUtil.extractUserID(token);

        if (userId == null || role == null || role.equals("COMPANY") || role.equals("DELIVERY")) {
            throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
        }
        return deliveryRouteService.deleteDeliveryRoute(routeId, token);
    }
}
