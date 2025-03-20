package com.sibijo.hub_routes.domain.model;

import com.sibijo.common.entity.BaseEntity;
import com.sibijo.common.exception.CustomException;
import com.sibijo.hub_routes.domain.exception.HubRoutesDomainExceptionCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "p_hub_routes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false")
@SQLDelete(sql = "UPDATE p_hub_routes SET is_deleted = true WHERE hub_routes_id = ?")
public class HubRoutesEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "hub_routes_id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "departure_id", nullable = false)
    private UUID departureId;

    @Column(name = "destination_id", nullable = false)
    private UUID destinationId;

    @Column(name = "central_id", nullable = false)
    private UUID centralId;

    @Column(name = "distance", precision = 10, scale = 2, nullable = false)
    private BigDecimal distance;

    @Column(name = "estimated_time", nullable = false)
    private Integer estimatedTime;

    @Builder
    private HubRoutesEntity(
            UUID departureId,
            UUID destinationId,
            UUID centralId,
            BigDecimal distance,
            Integer estimatedTime
    ) {
        this.departureId = departureId;
        this.destinationId = destinationId;
        this.centralId = centralId;
        this.distance = distance;
        this.estimatedTime = estimatedTime;
    }

    public void updateDestinationId(UUID destinationId) {
        if (destinationId != null) {
            this.destinationId = destinationId;
        }
        throw new CustomException(HubRoutesDomainExceptionCode.INVALID_HUB_ID);
    }

    public void updateDepartureId(UUID departureId) {
        if (departureId != null) {
            this.departureId = departureId;
        }
        throw new CustomException(HubRoutesDomainExceptionCode.INVALID_HUB_ID);
    }

    public void updateRoutes(UUID centralId, BigDecimal distance, Integer estimatedTime) {
        this.centralId = centralId;
        this.distance = distance;
        this.estimatedTime = estimatedTime;
    }


}
