package com.sibijo.delivery.domain.repository;

import com.sibijo.delivery.domain.entity.Delivery;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryRepository {

    List<Delivery> findAll();

    Optional<Delivery> findById(Long id);

    Delivery save(Delivery delivery);

}
