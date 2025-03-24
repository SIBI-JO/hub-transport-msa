package com.sibijo.delivery.application.dto;

import com.sibijo.delivery.domain.entity.Delivery;
import com.sibijo.delivery.domain.entity.DeliveryRoute;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRouteResponseDto {
    private UUID routeId;
    private UUID deliveryId;
    private Integer sequence;
    private String routeSequence;
    private UUID startHubId;
    private UUID endHubId;
    private String expectedDistance;
    private String expectedDuration;
    private String realDistance;
    private String realDuration;
    private String deliveryStatus;
    private Long deliveryManagerId;

    public DeliveryRouteResponseDto(DeliveryRoute deliveryRoute) {

        this.routeId = deliveryRoute.getRouteId();
        this.deliveryId = deliveryRoute.getDelivery().getDeliveryId();
        this.sequence = deliveryRoute.getSequence();
        this.routeSequence = deliveryRoute.getRouteSequence();
        this.startHubId = deliveryRoute.getStartHubId();
        this.endHubId = deliveryRoute.getEndHubId();
        this.expectedDistance = deliveryRoute.getExpectedDistance();
        this.expectedDuration = deliveryRoute.getExpectedDuration();
        this.realDistance = deliveryRoute.getRealDistance();
        this.realDuration = deliveryRoute.getRealDuration();

    }
}
