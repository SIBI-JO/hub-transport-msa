package com.sibijo.user.presentation.dto.deliveryAgent;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeliveryAgentPageResponseDto {

    private int page;
    private int size;
    private int total;

    private List<DeliveryAgentSearchDetailsResponseDto> users;
}
