package com.sibijo.delivery.presentation.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRequestDto {
    private UUID startHubId;
    private UUID endHubId;
    private String deliveryAddress;
    private String receiver;
    private String receiverSlackId;

}
