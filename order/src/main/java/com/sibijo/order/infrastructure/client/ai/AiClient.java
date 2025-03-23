package com.sibijo.order.infrastructure.client.ai;

import com.sibijo.order.infrastructure.client.ai.AiNotificationRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "ai-service")
public interface AiClient {

    @PostMapping("/api/ai/orders/dm")
    void notifyOrderCreated(
            AiNotificationRequestDto dto,
            @RequestHeader("Authorization") String bearerToken
    );
}
