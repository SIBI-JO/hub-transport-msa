package com.sibijo.delivery.infrastructure.repository;

import com.sibijo.delivery.domain.entity.DeliveryRoute;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface DeliveryRouteRepositoryCustom {

    @Query("SELECT o FROM DeliveryRoute o WHERE (o.startHubId = :hubId OR o.endHubId = :hubId) AND o.deletedAt IS NULL")
    Page<DeliveryRoute> findDeliveriesByHubId(UUID hubId, Pageable pageable);

}
