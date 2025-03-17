package com.sibijo.order.application.service;

import com.sibijo.order.domain.entity.Order;
import com.sibijo.order.domain.repository.OrderRepository;
import com.sibijo.order.infrastructure.client.Company.CompanyClient;
import com.sibijo.order.infrastructure.client.Company.CompanyResponseDto;
import com.sibijo.order.infrastructure.client.Delivery.DeliveryClient;
import com.sibijo.order.infrastructure.client.Delivery.DeliveryCreateRequestDto;
import com.sibijo.order.infrastructure.client.Delivery.DeliveryRequestDto;
import com.sibijo.order.infrastructure.client.Delivery.DeliveryResponseDto;
import com.sibijo.order.infrastructure.client.Delivery.DeliveryRouteRequestDto;
import com.sibijo.order.infrastructure.client.Hub.HubClient;
import com.sibijo.order.infrastructure.client.Hub.HubResponseDto;
import com.sibijo.order.infrastructure.client.Product.ProductClient;
import com.sibijo.order.infrastructure.client.Product.ProductResponseDto;
import com.sibijo.order.presentation.dto.OrderRequestDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "주문 Service")
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final DeliveryClient deliveryClient;
    private final HubClient hubClient;
    private final ProductClient productClient;
    private final CompanyClient companyClient;

    @Transactional
    public void createOrder(OrderRequestDto requestDto, String userId) {

        // 상품 서버에서 재고 확인
        ProductResponseDto product = productClient.getProductStock(requestDto.getProductId());

        if (product.getAmount() < requestDto.getAmount()) {
            throw new NullPointerException("재고 부족하여 주문을 진행할 수 없습니다.");
        }

        // 주문 때 들어온 공급업체와 수령업체 정보를 통해 해당하는 출발/도착 허브 조회
        UUID startHubId = product.getHubId();   // 상품 서버 내부의 허브 재고 확인 과정에서 허브 정보도 같이 받아왔음
        CompanyResponseDto endHub = companyClient.getHubByCompanyId(requestDto.getRecipientsId());

        // 허브 서버에 허브 아이디 보내서 두 허브간 경로 데이터 받아오기
        HubResponseDto hubRoute = hubClient.getHubRouteForOrder(startHubId, endHub.getHubId());


        // 배송 정보 & 배송 경로 정보 Dto 만들기
        DeliveryRequestDto deliveryRequest = new DeliveryRequestDto(
                startHubId,
                endHub.getHubId(),
                endHub.getDeliveryAddress(),
                requestDto.getReceiver(),
                requestDto.getReceiverSlcakId(),
                null  // 배송 담당자는 아직 없음
        );

        DeliveryRouteRequestDto deliveryRouteRequest = new DeliveryRouteRequestDto(
                1L, // 시퀀스 임의 설정
                startHubId,
                endHub.getHubId(),
                hubRoute.getExpectedDistance(),
                hubRoute.getExpectedTime()
        );

        DeliveryCreateRequestDto createRequestDto = new DeliveryCreateRequestDto(
                deliveryRequest,
                deliveryRouteRequest
        );


        // 배송 서버로 데이터를 보내서 배송 생성
        DeliveryResponseDto delivery = deliveryClient.createDelivery(createRequestDto);

        // 배송 정보를 넣어서 주문 생성
        Order order = Order.createOrder(requestDto, delivery.getDeliveryId());

        orderRepository.save(order);

    }
}
