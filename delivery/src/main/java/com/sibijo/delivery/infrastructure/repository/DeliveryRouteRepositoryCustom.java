package com.sibijo.delivery.infrastructure.repository;

import com.sibijo.delivery.domain.entity.DeliveryRoute;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeliveryRouteRepositoryCustom {

    @Query("SELECT o FROM DeliveryRoute o WHERE (o.startHubId = :hubId OR o.endHubId = :hubId) AND o.deletedAt IS NULL")
    Page<DeliveryRoute> findDeliveriesByHubId(UUID hubId, Pageable pageable);


    @Query("SELECT dr FROM DeliveryRoute dr WHERE " +
            "(:recipientsId IS NULL OR dr.recipientsId = :recipientsId) AND " +
            "(:deliveryManagerId IS NULL OR dr.deliveryManagerId = :deliveryManagerId) AND " +
            "dr.deletedAt IS NULL")
    Page<DeliveryRoute> searchDeliveryRoutes(@Param("recipientsId") UUID recipientsId,
            @Param("deliveryManagerId") Long deliveryManagerId,
            Pageable pageable);




    @Query("SELECT dr FROM DeliveryRoute dr WHERE " +
            "(dr.startHubId = :hubId OR dr.endHubId = :hubId) AND " +
            "(:recipientsId IS NULL OR dr.recipientsId = :recipientsId) AND " +
            "(:deliveryManagerId IS NULL OR dr.deliveryManagerId = :deliveryManagerId) AND " +
            "dr.deletedAt IS NULL")
    Page<DeliveryRoute> searchDeliveryRoutesByHub(@Param("hubId") UUID hubId,
            @Param("recipientsId") UUID recipientsId,
            @Param("deliveryManagerId") Long deliveryManagerId,
            Pageable pageable);



    @Query("SELECT dr FROM DeliveryRoute dr WHERE " +
            "dr.deliveryManagerId = :userId AND " +
            "(:recipientsId IS NULL OR dr.recipientsId = :recipientsId) AND " +
            "dr.deletedAt IS NULL")
    Page<DeliveryRoute> searchDeliveryRoutesByDeliveryManager(@Param("userId") Long userId,
            @Param("recipientsId") UUID recipientsId,
            Pageable pageable);



    @Query("SELECT dr FROM DeliveryRoute dr WHERE " +
            "dr.recipientsId = :companyId AND " +
            "(:deliveryManagerId IS NULL OR dr.deliveryManagerId = :deliveryManagerId) AND " +
            "dr.deletedAt IS NULL")
    Page<DeliveryRoute> searchDeliveryRoutesByCompany(@Param("companyId") UUID companyId,
            @Param("deliveryManagerId") Long deliveryManagerId,
            Pageable pageable);
}
