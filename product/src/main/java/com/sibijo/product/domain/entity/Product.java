package com.sibijo.product.domain.entity;

import com.sibijo.common.entity.BaseEntity;
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
public class Product extends BaseEntity {

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


    public Product(String productName, Integer price, UUID companyId) {
        this.productName = productName;
        this.price = price;
        this.companyId = companyId;
    }
}
