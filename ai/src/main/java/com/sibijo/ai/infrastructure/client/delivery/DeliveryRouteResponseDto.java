package com.sibijo.ai.infrastructure.client.delivery;

import java.util.UUID;
import lombok.Data;

@Data
public class DeliveryRouteResponseDto {
    private UUID deliveryId;
    private String expectedDistance;
    private String expectedDuration;

}
