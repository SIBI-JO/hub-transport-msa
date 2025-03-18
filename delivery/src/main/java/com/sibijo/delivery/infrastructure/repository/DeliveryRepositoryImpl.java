package com.sibijo.delivery.infrastructure.repository;

import com.sibijo.delivery.domain.entity.Delivery;
import com.sibijo.delivery.domain.repository.DeliveryRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepositoryImpl extends JpaRepository<Delivery, UUID>, DeliveryRepository {

}
