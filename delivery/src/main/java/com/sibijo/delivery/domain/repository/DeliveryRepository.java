package com.sibijo.delivery.domain.repository;

import com.sibijo.delivery.domain.entity.Delivery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryRepository {

    List<Delivery> findAll();

    Optional<Delivery> findById(UUID id);

    Delivery save(Delivery delivery);

}
