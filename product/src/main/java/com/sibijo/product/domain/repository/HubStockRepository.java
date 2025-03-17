package com.sibijo.product.domain.repository;

import com.sibijo.product.domain.entity.HubStock;
import com.sibijo.product.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface HubStockRepository extends JpaRepository<HubStock, UUID> {
    Optional<HubStock> findByProductAndHubId(Product product, UUID hubId);
    Optional<HubStock> findByProduct(Product product);
}
