package com.sibijo.product.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "p_product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(name = "product_name", length = 50, nullable = false)
    private String productName;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "hub_id", nullable = false)
    private Long hubId;

}
