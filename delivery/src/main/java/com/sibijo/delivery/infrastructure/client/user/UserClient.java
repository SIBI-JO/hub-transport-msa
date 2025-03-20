package com.sibijo.delivery.infrastructure.client.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/api/delivery-agents/hub")
    UserResponseDto getDeliveryAgent();

}
