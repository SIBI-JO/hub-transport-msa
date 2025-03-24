package com.sibijo.ai.infrastructure.client.user;

import com.sibijo.common.dto.ApiResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service", contextId = "deliveryAgentServiceClient")
public interface DeliveryAgentServiceClient {

    @GetMapping("/api/users/{id}")
    ApiResponse<DeliveryAgentDetailsResponseDto> getDeliveryAgentById(
            @PathVariable("id") Long id,
            @RequestHeader("Authorization") String bearerToken
    );
}
