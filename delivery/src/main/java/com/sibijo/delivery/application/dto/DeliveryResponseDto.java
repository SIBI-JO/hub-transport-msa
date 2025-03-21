package com.sibijo.delivery.application.dto;

import com.sibijo.delivery.domain.entity.Delivery;
import com.sibijo.delivery.domain.enums.DeliveryStatusEnum;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryResponseDto {

    private UUID deliveryId;
    private DeliveryStatusEnum deliveryStatus;
    private UUID startHubId;
    private UUID endHubId;
    private String deliveryAddress;
    private String receiver;
    private String receiverSlackId;
    private UUID recipientsId;
    private Long deliveryManagerId;

    public DeliveryResponseDto(Delivery delivery) {
        this.deliveryId = delivery.getDeliveryId();
        this.deliveryStatus = delivery.getDeliveryStatus();
        this.startHubId = delivery.getStartHubId();
        this.endHubId = delivery.getEndHubId();
        this.deliveryAddress = delivery.getDeliveryAddress();
        this.receiver = delivery.getReceiver();
        this.receiverSlackId = delivery.getReceiverSlackId();
        this.recipientsId = delivery.getRecipientsId();
        this.deliveryManagerId = delivery.getDeliveryManagerId();
    }

}
