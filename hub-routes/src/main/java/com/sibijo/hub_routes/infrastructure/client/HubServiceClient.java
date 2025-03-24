package com.sibijo.hub_routes.infrastructure.client;

import com.sibijo.hub_routes.infrastructure.dto.HubServiceClientDto;
import com.sibijo.hub_routes.infrastructure.dto.HubServiceResponseDto;
import java.util.List;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "hub-service")
public interface HubServiceClient {

    @GetMapping("/api/hubs/hub-routes")
    HubServiceClientDto getHubForHubRoutes();

    @GetMapping("/api/hubs/central-hub")
    List<HubServiceResponseDto> getCentralHub();
}
