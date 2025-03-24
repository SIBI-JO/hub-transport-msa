package com.sibijo.ai.infrastructure.client.delivery;

import java.util.UUID;
import lombok.Data;

@Data
public class DeliveryRouteResponseDto {
    private UUID deliveryId;
    private String realDistance; // 실제 거리
    private String realDuration; // 실제 소요 시간

}
