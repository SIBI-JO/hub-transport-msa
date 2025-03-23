package com.sibijo.order.domain.repository;

import com.sibijo.order.domain.entity.Order;
import com.sibijo.order.domain.enums.OrderStatusEnum;
import com.sibijo.order.infrastructure.repository.OrderRepositoryCustom;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface OrderRepository extends JpaRepository<Order, UUID>, OrderRepositoryCustom {

    List<Order> findAll();

    Optional<Order> findById(UUID id);

    Order save(Order user);

    Page<Order> findAllByDeletedAtIsNullAndOrderStatus(OrderStatusEnum statusEnum, Pageable pageable);

    // 배송 담당자(DELIVERY) - 본인의 허브와 관련된 주문 중 COMPLETED 상태인 것만 조회
    Page<Order> findByOrdererIdAndDeletedAtIsNullAndOrderStatus(Long ordererId, OrderStatusEnum status, Pageable pageable);

}
