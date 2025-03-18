package com.sibijo.delivery.application.dto;

import com.sibijo.delivery.domain.entity.Delivery;
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

    public DeliveryResponseDto(Delivery delivery) {
        this.deliveryId = delivery.getDeliveryId();
        this.startHubId = delivery.getStartHubId();
        this.endHubId = delivery.getEndHubId();
        this.deliveryAddress = delivery.getDeliveryAddress();
        this.receiver = delivery.getReceiver();
        this.receiverSlackId = delivery.getReceiverSlackId();
        this.deliveryManagerId = delivery.getDeliveryManagerId();
    }

}
