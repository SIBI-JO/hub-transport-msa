package com.sibijo.product.repository;

import com.sibijo.product.entity.HubStock;
import com.sibijo.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface HubStockRepository extends JpaRepository<HubStock, UUID> {
    Optional<HubStock> findByProductAndHubId(Product product, UUID hubId);
    Optional<HubStock> findByProduct(Product product);
}
