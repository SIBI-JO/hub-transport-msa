package com.sibijo.delivery.presentation.dto;

import com.sibijo.delivery.domain.entity.Delivery;
import com.sibijo.delivery.domain.enums.DeliveryStatusEnum;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRouteUpdateRequestDto {

    private Delivery delivery;
    private Integer sequence;
    private UUID startHubId;
    private UUID endHubId;
    private String expectedDistance;
    private String expectedDuration;
    private String realDistance;
    private String realDuration;
    private DeliveryStatusEnum deliveryStatus;
    private Long deliveryManagerId;

}
