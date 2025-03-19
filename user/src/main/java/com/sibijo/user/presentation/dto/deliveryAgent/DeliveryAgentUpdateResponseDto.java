package com.sibijo.user.presentation.dto.deliveryAgent;

import com.sibijo.user.domain.enums.DeliveryType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeliveryAgentUpdateResponseDto {
    private Long userId;
    private String hubId;
    private DeliveryType deliveryType;
    private int deliveryOrder;
}
