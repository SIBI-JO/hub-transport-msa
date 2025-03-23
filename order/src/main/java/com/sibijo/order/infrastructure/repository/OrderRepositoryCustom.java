package com.sibijo.order.infrastructure.repository;

import com.sibijo.order.domain.entity.Order;
import com.sibijo.order.domain.enums.OrderStatusEnum;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



public interface OrderRepositoryCustom {

    @Query("SELECT o FROM Order o WHERE (o.supplierHubId = :hubId OR o.recipientHubId = :hubId) " +
            "AND o.deletedAt IS NULL AND o.orderStatus = 'COMPLETED'")
    Page<Order> findOrdersByHubId(@Param("hubId") UUID hubId, Pageable pageable);

}
