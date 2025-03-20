package com.sibijo.delivery.infrastructure.client.user;

import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/api/delivery-agents/hub")
    Long getDeliveryAgent();

    @GetMapping("/api/delivery-agents/hub/{hubId}")
    Long getCompanyDeliveryAgent(@PathVariable UUID hubId);

}
