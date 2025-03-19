package com.sibijo.delivery.infrastructure.repository;

import com.sibijo.delivery.domain.entity.Delivery;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeliveryRepositoryCustom {

    @Query("SELECT o FROM Delivery o WHERE (o.startHubId = :hubId OR o.endHubId = :hubId) AND o.deletedAt IS NULL")
    Page<Delivery> findDeliveriesByHubId(@Param("hubId") UUID hubId, Pageable pageable);

}
