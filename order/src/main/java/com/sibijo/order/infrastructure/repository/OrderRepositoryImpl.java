package com.sibijo.order.infrastructure.repository;

import com.sibijo.order.domain.entity.Order;
import com.sibijo.order.domain.repository.OrderRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepositoryImpl extends JpaRepository<Order, UUID>, OrderRepository {

}
