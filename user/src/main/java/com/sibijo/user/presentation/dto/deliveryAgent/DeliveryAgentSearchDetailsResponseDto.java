package com.sibijo.user.presentation.dto.deliveryAgent;

import com.sibijo.user.domain.enums.DeliveryType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DeliveryAgentSearchDetailsResponseDto {

    private Long userId;
    private String hubId;
    private DeliveryType deliveryType;
    private int deliveryOrder;
    private Boolean isDeleted;
}
