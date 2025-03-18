package com.sibijo.order.domain.repository;

import com.sibijo.order.domain.entity.Order;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository {

    List<Order> findAll();

    Optional<Order> findById(UUID id);

    Order save(Order user);

}
