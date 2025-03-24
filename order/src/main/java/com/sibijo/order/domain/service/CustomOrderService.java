package com.sibijo.order.domain.service;

import com.sibijo.order.domain.entity.Order;
import com.sibijo.order.domain.repository.OrderRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOrderService {

    private final OrderRepository orderRepository;

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
