package com.sibijo.hub.domain.model;


import com.sibijo.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "p_hub")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("is_deleted = false")
@SQLDelete(sql = "UPDATE p_hub SET is_deleted = true WHERE id = ?")
public class HubEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "hub_id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "hub_name", nullable = false)
    private String hubName;

    @Column(name = "hub_location", nullable = false)
    private String hubLocation;

    @Column(name = "latitude", precision = 10, scale = 7, nullable = false)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7, nullable = false)
    private BigDecimal longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "hub_type", nullable = false)
    private HubType hubType;
}
