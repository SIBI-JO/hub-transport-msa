package com.sibijo.order.application.service;

import com.sibijo.order.domain.entity.Order;
import com.sibijo.order.domain.repository.OrderRepository;
import com.sibijo.order.infrastructure.client.Delivery.DeliveryClient;
import com.sibijo.order.infrastructure.client.Delivery.DeliveryRequestDto;
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
    private final ProductClient productClient;

    @Transactional
    public void createOrder(OrderRequestDto requestDto, String userId) {

        // 상품 서버에서 재고 확인
        ProductResponseDto product = productClient.getProductStock(requestDto.getProductId());

        if (product.getAmount() < requestDto.getAmount()) {
            throw new NullPointerException("재고 부족하여 주문을 진행할 수 없습니다.");
        }

        // 주문 생성 및 저장
        Order order = Order.createOrder(requestDto);
        orderRepository.save(order);

        // 배송 생성에 필요한 데이터를 묶음
        DeliveryRequestDto deliveryRequestDto = new DeliveryRequestDto(
                order.getOrderId(),
                requestDto.getSupplierId(),
                requestDto.getRecipientsId(),
                requestDto.getReceiver(),
                requestDto.getReceiverSlackId()
        );

        // 배송 서버로 데이터를 보내서 배송 생성
        deliveryClient.createDelivery(deliveryRequestDto);

    }

    @Transactional
    public void updateOrderWithDelivery(UUID orderId, UUID deliveryId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        order.updateDelivery(deliveryId);

    }

}
