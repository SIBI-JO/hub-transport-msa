package com.sibijo.delivery.application.service;

import com.sibijo.delivery.application.dto.DeliveryResponseDto;
import com.sibijo.delivery.domain.entity.Delivery;
import com.sibijo.delivery.domain.entity.DeliveryRoute;
import com.sibijo.delivery.domain.service.DeliveryRouteService;
import com.sibijo.delivery.domain.service.DeliveryService;
import com.sibijo.delivery.infrastructure.client.company.CompanyClient;
import com.sibijo.delivery.infrastructure.client.company.CompanyResponseDto;
import com.sibijo.delivery.infrastructure.client.hub.HubClient;
import com.sibijo.delivery.infrastructure.client.hub.HubResponseDto;
import com.sibijo.delivery.infrastructure.client.order.OrderClient;
import com.sibijo.delivery.presentation.dto.DeliveryRequestDto;
import com.sibijo.delivery.presentation.dto.DeliveryRouteRequestDto;
import com.sibijo.delivery.presentation.dto.OrderToDeliveryRequestDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j(topic = "배송 통합 Service")
@Service
@RequiredArgsConstructor
public class CustomDeliveryService {

    private final DeliveryService deliveryService;
    private final DeliveryRouteService deliveryRouteService;
    private final CompanyClient companyClient;
    private final HubClient hubClient;
    private final OrderClient orderClient;


    public void createDelivery(OrderToDeliveryRequestDto requestDto, String userId) {

        // 1. 공급업체 & 수령업체 정보를 통해 출발/도착 허브 조회
        CompanyResponseDto startHub = companyClient.getHubByCompanyId(requestDto.getSupplierId());
        CompanyResponseDto endHub = companyClient.getHubByCompanyId(requestDto.getRecipientsId());

        // 2. 허브 서버에서 허브 간 경로 조회
        HubResponseDto hubRoute = hubClient.getHubRouteForOrder(startHub.getHubId(), endHub.getHubId());

        // 3. 배송 생성에 필요한 정보 생성
        DeliveryRequestDto deliveryRequestDto = new DeliveryRequestDto(
                startHub.getHubId(),
                endHub.getHubId(),
                endHub.getDeliveryAddress(),
                requestDto.getReceiver(),
                requestDto.getReceiverSlackId()
        );

        // 4. 배송 정보 생성 및 저장
        Delivery delivery = deliveryService.createDelivery(deliveryRequestDto);

        // 5. 주문 서버로 배송 ID 보내기
        orderClient.updateOrderWithDelivery(requestDto.getOrderId(), delivery.getDeliveryId());

        // 5. 배송 경로 생성에 필요한 정보 생성
        DeliveryRouteRequestDto routeRequestDto = new DeliveryRouteRequestDto(
                1L,
                startHub.getHubId(),
                endHub.getHubId(),
                hubRoute.getExpectedDistance(),
                hubRoute.getExpectedTime()
        );

        // 6. 배송 경로 생성
        DeliveryRoute deliveryRoute = deliveryRouteService.createDeliveryRoute(routeRequestDto, delivery);

    }


    /**
     *  배송 전체 조회
     *  권한 : Hub_Manager -> 자신의 허브만    //   Delivery_Manager -> 자신의 배송만
     *                                      // Company_Manager -> 자신의 업체만
     */




    /**
     *  배송 상세 조회
     *  권한 : Hub_Manager -> 자신의 허브만    //   Delivery_Manager -> 자신의 배송만
     *                                      // Company_Manager -> 자신의 업체만
     */
    public DeliveryResponseDto getDeliveryDetails(UUID deliveryId, String userId) {
        return deliveryService.getDeliveryDetails(deliveryId);
    }


    /**
     *  배송 수정
     *  권한 : Hub_Manager -> 자신의 허브만    //   Delivery_Manager -> 자신의 배송만
     *  업체 배송 담당자 수정을 어떻게 처리해야하는가
     *  -> 배송을 만들 때 유저 서버로 업체ID를 보내서 업체 관계자 정보 받아서 넣어야 하나?
     */




    /**
     *  배송 취소
     *  권한 : Hub_Manager -> 자신의 허브만
     */




    /*******************************************************************
     *  배송 경로 전체 조회
     *  권한 : Hub_Manager -> 자신의 허브만    //   Delivery_Manager -> 자신의 배송만
     *                                      // Company_Manager -> 자신의 업체만
     */




    /**
     *  배송 경로 상세 조회
     *  권한 : Hub_Manager -> 자신의 허브만    // Delivery_Manager -> 자신의 배송만
     *                                      // Company_Manager -> 자신의 업체만
     */


    /**
     *  배송 경로 수정
     *  권한 : Hub_Manager -> 자신의 허브만    //   Delivery_Manager -> 자신의 배송만
     *  실제 거리 / 실제 소요 시간만 수정 가능? 아니면 다른 부분도 수정 가눙?
     */


    /**
     *  배송 경로 삭제
     *  권한 : Hub_Manager -> 자신의 허브만
     */
}
