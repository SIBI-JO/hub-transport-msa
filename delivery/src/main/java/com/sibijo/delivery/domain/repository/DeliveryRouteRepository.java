package com.sibijo.delivery.domain.repository;

import com.sibijo.delivery.domain.entity.DeliveryRoute;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryRouteRepository {

    List<DeliveryRoute> findAll();

    Optional<DeliveryRoute> findById(UUID id);

    DeliveryRoute save(DeliveryRoute delivery);

}
