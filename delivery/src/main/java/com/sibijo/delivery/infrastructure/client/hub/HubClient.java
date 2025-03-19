package com.sibijo.delivery.infrastructure.client.hub;

import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "hub-service")
public interface HubClient {

    @GetMapping("/api/hubs/hub-routes/order")
    HubResponseDto getHubRouteForOrder(@RequestParam UUID startHubId, @RequestParam UUID endHubId);

}
