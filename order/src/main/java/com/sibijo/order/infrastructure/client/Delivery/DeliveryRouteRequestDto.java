package com.sibijo.order.infrastructure.client.Delivery;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRouteRequestDto {

    private Long sequence;
    private UUID startHubId;
    private UUID endHubId;
    private String expectedDistance;
    private String expectedTime;

}
