package com.sibijo.ai.infrastructure.client.user;

import lombok.Data;

@Data
public class DeliveryAgentDetailsResponseDto {
    private Long id;
    private String name;
    private String slackUserId;
}
