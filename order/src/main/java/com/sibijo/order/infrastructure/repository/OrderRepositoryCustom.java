package com.sibijo.order.infrastructure.repository;

import com.sibijo.order.domain.entity.Order;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepositoryCustom {

    @Query("SELECT o FROM Order o WHERE (o.supplierHubId = :hubId OR o.recipientHubId = :hubId) " +
            "AND o.deletedAt IS NULL AND o.orderStatus = 'COMPLETED'")
    Page<Order> findOrdersByHubId(@Param("hubId") UUID hubId, Pageable pageable);

}
