package com.sibijo.ai.infrastructure.client.delivery;


import lombok.Data;
import java.util.UUID;

@Data
public class DeliveryDetailsDto {
    private UUID deliveryId;
    private UUID startHubId;   // 출발 허브 ID
    private UUID endHubId;     // 도착 허브 ID
}
