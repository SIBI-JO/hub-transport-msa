package com.sibijo.product.domain.repository;

import com.sibijo.product.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query("SELECT p FROM Product p " +
            "WHERE (:productName IS NULL OR LOWER(p.productName) LIKE CONCAT('%', LOWER(:productName), '%')) " +
            "AND (:price IS NULL OR p.price = :price) " +
            "AND p.isDeleted = false")
    Page<Product> searchProducts(@Param("productName") String productName,
            @Param("price") Integer price,
            Pageable pageable);
}
