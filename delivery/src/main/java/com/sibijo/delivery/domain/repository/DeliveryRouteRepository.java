package com.sibijo.delivery.domain.repository;

import com.sibijo.delivery.domain.entity.Delivery;
import com.sibijo.delivery.domain.entity.DeliveryRoute;
import com.sibijo.delivery.infrastructure.repository.DeliveryRepositoryCustom;
import com.sibijo.delivery.infrastructure.repository.DeliveryRouteRepositoryCustom;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryRouteRepository extends JpaRepository<DeliveryRoute, UUID>,
        DeliveryRouteRepositoryCustom {

    List<DeliveryRoute> findAll();

    Optional<DeliveryRoute> findById(UUID id);

    DeliveryRoute save(DeliveryRoute delivery);

    Page<DeliveryRoute> findAllByDeletedAtIsNull(Pageable pageable);
    
    Page<DeliveryRoute> findByDeliveryManagerIdAndDeletedAtIsNull(Long userId, Pageable pageable);

    Page<DeliveryRoute> findByEndHubIdAndDeletedAtIsNull(UUID hubId, Pageable pageable);
}
