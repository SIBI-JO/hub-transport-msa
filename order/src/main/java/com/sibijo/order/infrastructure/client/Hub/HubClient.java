package com.sibijo.order.infrastructure.client.Hub;

import java.util.UUID;
import lombok.Getter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "hub-service")
public interface HubClient {

    @GetMapping("/api/hubs/hub-routes/order")
    HubResponseDto getHubRouteForOrder(@RequestBody UUID startHubId, @RequestBody UUID endHubId);

}
