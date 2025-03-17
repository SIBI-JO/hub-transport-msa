package com.sibijo.delivery.infrastructure.repository;

import com.sibijo.delivery.domain.entity.DeliveryRoute;
import com.sibijo.delivery.domain.repository.DeliveryRouteRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRouteRepositoryImpl extends JpaRepository<DeliveryRoute, UUID>,
        DeliveryRouteRepository {

}
