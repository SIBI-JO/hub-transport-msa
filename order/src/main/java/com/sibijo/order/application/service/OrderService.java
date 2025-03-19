package com.sibijo.order.application.service;

import com.sibijo.common.exception.CustomException;
import com.sibijo.order.application.dto.OrderResponseDto;
import com.sibijo.order.domain.entity.Order;
import com.sibijo.order.domain.repository.OrderRepository;
import com.sibijo.order.infrastructure.client.Delivery.DeliveryClient;
import com.sibijo.order.infrastructure.client.Delivery.DeliveryRequestDto;
import com.sibijo.order.infrastructure.client.Product.ProductClient;
import com.sibijo.order.infrastructure.client.Product.ProductResponseDto;
import com.sibijo.order.presentation.dto.OrderRequestDto;
import com.sibijo.order.presentation.dto.OrderUpdateRequestDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j(topic = "주문 Service")
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final DeliveryClient deliveryClient;
    private final ProductClient productClient;

    /**
     *   주문 생성
     */
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

    /**
     *   주문에 배송 정보 업데이트
     */
    @Transactional
    public void updateOrderWithDelivery(UUID orderId, UUID deliveryId) {

        Order order = orderRepository.findById(orderId)
                .filter(o -> o.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found or has been deleted"));

        order.updateDelivery(deliveryId);

    }

    /**
     *   주문 전체 조회
     *   미완성 : 1. 권한 별 조회, 2. 주문 상태가 완료 상태인 주문만 조회
     *   권한 : Hub_Manager -> 자신의 허브만, Delivery_Manager/Company_Manager -> 본인의 주문만
     */
//    @Transactional(readOnly = true)
//    public Page<OrderResponseDto> getOrders(String userId, String role, Pageable pageable) {
//        Page<Order> orderList = orderRepository.findByUserIdAndDeletedByIsNull(userId, pageable);
//
//        return orderList.map(OrderResponseDto::new);
//    }

    /**
     *   주문 상세 조회
     *   미완성 : 1. 권한 별 조회, 2. 주문 상태가 완료 상태인 주문만 조회
     *   권한 : Hub_Manager -> 자신의 허브만, Delivery_Manager/Company_Manager -> 본인의 주문만
     */
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(UUID orderId, String userId) {
        Order order = orderRepository.findById(orderId)
                .filter(o -> o.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found or has been deleted"));
        return new OrderResponseDto(order);
    }

    /**
     *   주문 수정
     *   미완성 : 1. 권한 별 수정, 2. 주문 상태가 완료 상태인 주문만 조회
     *   권한 : Hub_Manager -> 자신의 허브만 , Master -> All
     */
    @Transactional
    public OrderResponseDto updateOrder(UUID orderId, OrderUpdateRequestDto requestDto, String userId) {

        Order order = orderRepository.findById(orderId)
                .filter(o -> o.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found or has been deleted"));
        order.updateOrder(requestDto);
        Order updateOrder = orderRepository.save(order);
        return new OrderResponseDto(updateOrder);
    }

    /**
     *   주문 삭제
     *   미완성 : 1. 권한 별 접근, 2. 주문 상태가 완료 상태인 주문만 조회
     *   권한 : Hub_Manager -> 자신의 허브만 , Master -> All
     */
//    @Transactional
//    public void deleteOrder(UUID orderId, String userId) {
//        Order order = orderRepository.findById(orderId)
//                .filter(o -> o.getDeletedAt() == null)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found or has been deleted"));
//
//        orderRepository.save(order);
//    }
}
