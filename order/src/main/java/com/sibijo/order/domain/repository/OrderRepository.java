package com.sibijo.order.domain.repository;

import com.sibijo.order.domain.entity.Order;
import com.sibijo.order.domain.enums.OrderStatusEnum;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findAll();

    Optional<Order> findById(UUID id);

    Order save(Order user);

//    Page<Order> findAllByDeletedAtIsNullAndOrderStatus(OrderStatusEnum statusEnum, Pageable pageable);
//
//    // 배송 담당자(DELIVERY) - 본인의 허브와 관련된 주문 중 COMPLETED 상태인 것만 조회
//    Page<Order> findByOrdererIdAndDeletedAtIsNullAndOrderStatus(Long ordererId, OrderStatusEnum status, Pageable pageable);
//
//    @Query("SELECT o FROM Order o WHERE (o.supplierHubId = :hubId OR o.recipientHubId = :hubId) " +
//            "AND o.deletedAt IS NULL AND o.orderStatus = 'COMPLETED'")
//    Page<Order> findOrdersByHubId(@Param("hubId") UUID hubId, Pageable pageable);

    // 전체 조회 (MASTER)
    @Query("SELECT o FROM Order o WHERE o.deletedAt IS NULL AND o.orderStatus = 'COMPLETED' " +
            "AND (:ordererId IS NULL OR o.ordererId = :ordererId) " +
            "AND (:supplierId IS NULL OR o.supplierId = :supplierId) " +
            "AND (:recipientsId IS NULL OR o.recipientsId = :recipientsId)")
    Page<Order> searchOrders(@Param("ordererId") Long ordererId,
            @Param("supplierId") UUID supplierId,
            @Param("recipientsId") UUID recipientsId,
            Pageable pageable);

    // 허브 사용자 조회
    @Query("SELECT o FROM Order o WHERE o.deletedAt IS NULL AND o.orderStatus = 'COMPLETED' " +
            "AND (o.supplierHubId = :hubId OR o.recipientHubId = :hubId) " +
            "AND (:ordererId IS NULL OR o.ordererId = :ordererId) " +
            "AND (:supplierId IS NULL OR o.supplierId = :supplierId) " +
            "AND (:recipientsId IS NULL OR o.recipientsId = :recipientsId)")
    Page<Order> searchOrdersForHub(@Param("hubId") UUID hubId,
            @Param("ordererId") Long ordererId,
            @Param("supplierId") UUID supplierId,
            @Param("recipientsId") UUID recipientsId,
            Pageable pageable);

    // 본인이 주문한 것만 보기 (DELIVERY, COMPANY)
    @Query("SELECT o FROM Order o WHERE o.deletedAt IS NULL AND o.orderStatus = 'COMPLETED' " +
            "AND o.ordererId = :ordererId " +
            "AND (:supplierId IS NULL OR o.supplierId = :supplierId) " +
            "AND (:recipientsId IS NULL OR o.recipientsId = :recipientsId)")
    Page<Order> searchOrdersByOrdererId(@Param("ordererId") Long ordererId,
            @Param("supplierId") UUID supplierId,
            @Param("recipientsId") UUID recipientsId,
            Pageable pageable);

}
