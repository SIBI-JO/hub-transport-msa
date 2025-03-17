package com.sibijo.order.domain.repository;

import com.sibijo.order.domain.entity.Order;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository {

    List<Order> findAll();

    Optional<Order> findById(Long id);

    Order save(Order user);

}
