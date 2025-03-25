package com.sibijo.hub_routes.domain.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sibijo.common.entity.BaseEntity;
import com.sibijo.common.exception.CustomException;
import com.sibijo.common.exception.codes.CommonExceptionCode;
import com.sibijo.hub_routes.domain.exception.HubRoutesDomainExceptionCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

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

    @Column(name = "distance", precision = 10, scale = 2, nullable = false)
    private BigDecimal distance;

    @Column(name = "estimated_time", nullable = false)
    private Integer estimatedTime;

    /**
     * 허브 이동 경로 (Map<Integer, String> 형태를 JSON으로 저장)
     */
    @Column(name = "sequence", columnDefinition = "TEXT", nullable = false)
    private String sequence;

    @Column(name = "hash_sequence", nullable = false)
    private String hashSequence;

    @Builder
    private HubRoutesEntity(
            UUID departureId,
            UUID destinationId,
            BigDecimal distance,
            Integer estimatedTime,
            String sequence,
            String hashSequence
    ) {
        this.departureId = departureId;
        this.destinationId = destinationId;
        this.distance = distance;
        this.estimatedTime = estimatedTime;
        this.sequence = sequence;
        this.hashSequence = hashSequence;
    }

    /**
     * JSON을 Map<Integer, String>으로 변환
     *
     * @return
     */
    public Map<Integer, String> getRouteSequenceAsMap() {
        return convertJsonToMap(this.sequence);
    }

    /**
     * JSON 문자열 → Map<Integer, String> 변환
     *
     * @param json
     * @return Map<Integer, String>
     */
    private Map<Integer, String> convertJsonToMap(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, new TypeReference<Map<Integer, String>>() {
            });
        } catch (Exception e) {
            throw new CustomException(CommonExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    public void updateDestinationId(UUID destinationId) {
        if (destinationId == null) {
            throw new CustomException(HubRoutesDomainExceptionCode.INVALID_HUB_ID);
        }
        this.destinationId = destinationId;
        System.out.println("destinationId: " + destinationId);
    }

    public void updateDepartureId(UUID departureId) {
        if (departureId == null) {
            throw new CustomException(HubRoutesDomainExceptionCode.INVALID_HUB_ID);
        }
//        throw new CustomException(HubRoutesDomainExceptionCode.INVALID_HUB_ID);
        this.departureId = departureId;
        System.out.println("departureId=" + departureId);
    }

    public void updateDistance(BigDecimal distance) {
        System.out.println("distance: " + distance);
        if (distance == null) {
            throw new CustomException(HubRoutesDomainExceptionCode.INVALID_HUB_ROUTE_DISTANCE);
        }
        this.distance = distance;
    }

    public void updateEstimatedTime(Integer estimatedTime) {
        if (estimatedTime == null) {
            throw new CustomException(HubRoutesDomainExceptionCode.INVALID_HUB_ROUTE_TIME);
        }
        this.estimatedTime = estimatedTime;
    }

    public void updateRoutes(BigDecimal distance, Integer estimatedTime) {
        this.distance = distance;
        this.estimatedTime = estimatedTime;
    }


}
