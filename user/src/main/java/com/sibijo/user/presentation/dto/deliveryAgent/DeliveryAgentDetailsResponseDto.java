package com.sibijo.user.presentation.dto.deliveryAgent;

import com.sibijo.user.domain.enums.DeliveryType;
import com.sibijo.user.domain.enums.Role;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeliveryAgentDetailsResponseDto {

    private Long userId;
    private UUID hubId;
    private DeliveryType deliveryType;
    private int deliveryOrder;

    //user
//    private String slackId;
//    private Role role;

}
