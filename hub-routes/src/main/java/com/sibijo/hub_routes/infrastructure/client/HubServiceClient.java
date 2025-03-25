package com.sibijo.hub_routes.infrastructure.client;

import com.sibijo.hub_routes.infrastructure.dto.HubServiceClientDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "hub-service", fallbackFactory = HubServiceClientFallback.class)
public interface HubServiceClient {

    @CircuitBreaker(name = "hub-service")
    @Retry(name = "hub-service-retry")
    @GetMapping("/api/hubs/hub-routes")
    HubServiceClientDto getHubForHubRoutes();

}
