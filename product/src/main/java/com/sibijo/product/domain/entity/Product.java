package com.sibijo.product.domain.entity;

import com.sibijo.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import java.util.UUID;

@Entity
@Table(name = "p_product")
@SQLDelete(sql = "UPDATE p_product SET is_deleted = true WHERE product_id = ?")
@Where(clause = "is_deleted = false")
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

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    public Product(String productName, Integer price, UUID companyId) {
        this.productName = productName;
        this.price = price;
        this.companyId = companyId;
    }
}
