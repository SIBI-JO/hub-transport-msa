package com.sibijo.order.infrastructure.client.ai;

import lombok.Data;

import java.util.UUID;

@Data
public class AiNotificationRequestDto {
    private UUID orderId;      // 주문 ID
    private String userSlackId; //  Slack ID
}
