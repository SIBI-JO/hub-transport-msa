package com.sibijo.order.infrastructure.client.Delivery;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryResponseDto {

    private UUID deliveryId;
    private UUID startHubId;
    private UUID endHubId;
    private String deliveryAddress;
    private String receiver;
    private String receiverSlackId;
    private UUID deliveryManagerId;

}
