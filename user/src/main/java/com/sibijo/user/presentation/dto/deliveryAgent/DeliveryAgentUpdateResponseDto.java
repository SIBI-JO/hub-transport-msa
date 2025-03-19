package com.sibijo.user.presentation.dto.deliveryAgent;

import com.sibijo.user.domain.enums.DeliveryType;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeliveryAgentUpdateResponseDto {
    private Long userId;
    private UUID hubId;
    private DeliveryType deliveryType;
    private int deliveryOrder;
}
