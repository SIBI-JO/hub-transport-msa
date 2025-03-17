package com.sibijo.product.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "p_product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID productId;

    @Column(name = "product_name", length = 50, nullable = false)
    private String productName;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;
}
