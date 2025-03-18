package com.sibijo.delivery.application.service;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "배송 통합 Service")
@Service
@RequiredArgsConstructor
public class DeliveryServiceCenter {

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
}
