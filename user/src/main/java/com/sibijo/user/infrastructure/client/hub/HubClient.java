package com.sibijo.user.infrastructure.client.hub;

import com.sibijo.common.dto.ApiResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "hub-service")
public interface HubClient {

    @GetMapping("/api/hubs/{hubId}/exists")
    ApiResponse hubExists(@PathVariable("hubId") UUID hubId);

}
