package com.sibijo.product.domain.entity;

import com.sibijo.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "p_hub_stock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HubStock extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID hubStockId;

    @Column(name = "hub_id", nullable = false)
    private UUID hubId;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "amount", nullable = false)
    private Long amount;


    public HubStock(UUID hubId, UUID companyId, Product product, Long amount) {
        this.hubId = hubId;
        this.companyId = companyId;
        this.product = product;
        this.amount = amount;
    }
}
