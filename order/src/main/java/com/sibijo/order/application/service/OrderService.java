package com.sibijo.order.application.service;

import com.sibijo.common.exception.CustomException;
import com.sibijo.common.exception.codes.CommonExceptionCode;
import com.sibijo.common.utils.Auth.AuthUtil;
import com.sibijo.common.utils.Auth.JwtUtil;
import com.sibijo.common.utils.page.PageableUtils;
import com.sibijo.order.application.dto.OrderResponseDto;
import com.sibijo.order.domain.entity.Order;
import com.sibijo.order.domain.enums.OrderStatusEnum;
import com.sibijo.order.domain.repository.OrderRepository;
import com.sibijo.order.infrastructure.client.Delivery.DeliveryClient;
import com.sibijo.order.infrastructure.client.Delivery.DeliveryRequestDto;
import com.sibijo.order.infrastructure.client.Product.ProductClient;
import com.sibijo.order.infrastructure.client.Product.UpdateStockRequestDto;
import com.sibijo.order.infrastructure.client.ai.AiClient;
import com.sibijo.order.infrastructure.client.ai.AiNotificationRequestDto;
import com.sibijo.order.presentation.dto.OrderCreateUpdateRequestDto;
import com.sibijo.order.presentation.dto.OrderRequestDto;
import com.sibijo.order.presentation.dto.OrderUpdateRequestDto;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;


@Slf4j(topic = "주문 Service")
@Service
@RequiredArgsConstructor
public class OrderService {
    private final JwtUtil jwtUtil;
    private final AuthUtil authUtil;
    private final OrderRepository orderRepository;
    private final DeliveryClient deliveryClient;
    private final ProductClient productClient;
    private final PlatformTransactionManager transactionManager;
    private final AiClient aiClient;

    /**
     *   주문 생성
     */
    public OrderResponseDto createOrder(OrderRequestDto requestDto, String token) {

        String role = jwtUtil.extractRole(token);
        Long userId = jwtUtil.extractUserID(token);

        // 권한 및 사용자 ID 검증
        if (role == null || userId == null) {
            throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
        }

        // 상품 서버에서 재고 확인
        Long amount = productClient.getProductOrderInfo(requestDto.getProductId()).getData().getAmount();

        if (amount < requestDto.getAmount().longValue()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "재고 부족하여 주문을 진행할 수 없습니다.");
        }

        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        // 3. 주문 저장
        Order order = transactionTemplate.execute(status -> {
            Order newOrder = Order.createOrder(requestDto, userId);
            orderRepository.save(newOrder);
            System.out.println("✅ 주문 생성 완료! 주문 ID: " + newOrder.getOrderId());
            return newOrder;
        });

        Long productAmount = amount - requestDto.getAmount().longValue();
        productClient.updateStock(order.getProductId(), new UpdateStockRequestDto(productAmount));

        DeliveryRequestDto deliveryRequestDto = new DeliveryRequestDto(
                order.getOrderId(),
                requestDto.getSupplierId(),
                requestDto.getRecipientsId(),
                requestDto.getReceiver(),
                requestDto.getReceiverSlackId(),
                token,
                requestDto.getProductId(),
                amount,
                requestDto.getAmount().longValue()
        );


        // 배송 서버 호출
        deliveryClient.createDelivery(deliveryRequestDto);

        return new OrderResponseDto(order);

    }

    /**
     *   주문에 배송 정보 업데이트
     */
    public void updateOrderWithDelivery(UUID orderId, OrderCreateUpdateRequestDto requestDto) {

        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        Order updatedOrder = transactionTemplate.execute(status -> {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order 없음"));

            // 주문 수정
            order.updateDelivery(requestDto);

            return order;
        });

        try {
            AiNotificationRequestDto aiDto = new AiNotificationRequestDto();
            aiDto.setOrderId(updatedOrder.getOrderId());
            aiDto.setUserSlackId(requestDto.getSlackId());

            // token을 헤더로 넘기기 위해 Feign 인터셉터나 메서드 파라미터로 전달
            aiClient.notifyOrderCreated(aiDto, requestDto.getToken());
        } catch (Exception e) {
            log.error("[AI 알림 실패] {}", e.getMessage());
            // 주문 생성 자체는 성공했으므로, 여기서는 예외 삼키고 넘어감
        }
    }

    /**
     *   주문 전체 조회
     *   미완성 :  2. 주문 상태가 완료 상태인 주문만 조회
     *   권한 : Hub_Manager -> 자신의 허브만, Delivery_Manager/Company_Manager -> 본인의 주문만
     */
    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getOrders(String token, Pageable pageable) {
        Pageable validatedPageable = PageableUtils.validatePageable(pageable);

        String role = jwtUtil.extractRole(token);
        Long userId = jwtUtil.extractUserID(token);
        UUID hubId = jwtUtil.extractHubIdForOrder(token);

        Page<Order> orderList = switch (role) {
            case "MASTER" -> orderRepository.findAllByDeletedAtIsNullAndOrderStatus(OrderStatusEnum.COMPLETED, validatedPageable);
            case "HUB" -> orderRepository.findOrdersByHubId(hubId, validatedPageable);
            case "DELIVERY", "COMPANY" -> orderRepository.findByOrdererIdAndDeletedAtIsNullAndOrderStatus(userId, OrderStatusEnum.COMPLETED, validatedPageable);
            default -> throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
        };

        return orderList.map(OrderResponseDto::new);
    }

    /**
     *   주문 상세 조회
     *   권한 : Hub_Manager -> 자신의 허브만, Delivery_Manager/Company_Manager -> 본인의 주문만
     */
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(UUID orderId, String token) {

        String role = jwtUtil.extractRole(token);
        UUID hubId = jwtUtil.extractHubIdForOrder(token);
        Long userId = jwtUtil.extractUserID(token);


        Order order = orderRepository.findById(orderId)
                .filter(o -> o.getDeletedAt() == null && o.getOrderStatus() == OrderStatusEnum.COMPLETED)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "주문이 없거나 삭제된 주문입니다."));

        switch (role) {
            case "HUB":
                if (!hubId.equals(order.getSupplierHubId()) && !hubId.equals(order.getRecipientHubId())) {
                    // 허브 담당자인데 공급업체나 수령업체의 허브 담당자가 아닐 때
                    throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
                }
                break;
            case "DELIVERY":
                System.out.println("사용자 ID :   "+userId);
                System.out.println("주문자 ID :   "+order.getOrdererId());
                if (!Objects.equals(userId, order.getOrdererId())) {
                    // 배송담당자 -> 만약 업체 배송 담당자라면 -> 본인이 담당 업체 배송 담당자인지 확인
                    throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
                }
                break;
            case "COMPANY":
                if (!Objects.equals(userId, order.getOrdererId())) {
                    throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
                }
        }

        return new OrderResponseDto(order);
    }

    /**
     *   주문 수정
     *   미완성 :  2. 주문 상태가 완료 상태인 주문만 조회?
     *   권한 : Hub_Manager -> 자신의 허브만 , Master -> All
     */
    @Transactional
    public OrderResponseDto updateOrder(UUID orderId, OrderUpdateRequestDto requestDto, String token) {
//        // JWT에서 Role 추출
        String role = jwtUtil.extractRole(token);
        UUID hubId = jwtUtil.extractHubIdForOrder(token);

        if (!role.equals("HUB") && !role.equals("MASTER")) {
            throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
        }


        Order order = orderRepository.findById(orderId)
                .filter(o -> o.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found or has been deleted"));


        if ((role.equals("HUB")) && !authUtil.isMyHubForOrder(hubId, order.getSupplierHubId(), order.getRecipientHubId())) {
            throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
        }

        order.updateOrder(requestDto);
        Order updateOrder = orderRepository.save(order);
        return new OrderResponseDto(updateOrder);
    }

    /**
     *   주문 삭제
     *   미완성 :  2. 주문 상태가 완료 상태인 주문만 조회?
     *   권한 : Hub_Manager -> 자신의 허브만 , Master -> All
     */
    @Transactional
    public OrderResponseDto deleteOrder(UUID orderId, String token) {

        String role = jwtUtil.extractRole(token);
        UUID hubId = jwtUtil.extractHubIdForOrder(token);

        if (!role.equals("HUB") && !role.equals("MASTER")) {
            throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
        }

        Order order = orderRepository.findById(orderId)
                .filter(o -> o.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found or has been deleted"));

        // 허브 담당자인데 공급업체나 수령업체의 허브 담당자가 아닐 때
        if ((role.equals("HUB")) && !authUtil.isMyHubForOrder(hubId, order.getSupplierHubId(), order.getRecipientHubId())) {
            throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
        }

        orderRepository.deleteById(orderId);
        return new OrderResponseDto(order);
    }


    /**
     *  배송 생성 실패 시, 임시 생성된 주문을 취소
     */
    @Transactional
    public void deleteOrderInternal(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NullPointerException("임시 생성된 주문이 없습니다."));

        orderRepository.deleteById(order.getOrderId());
        log.info("[System] 주문 내부 삭제 처리됨 - ID: {}", orderId);
    }
}
