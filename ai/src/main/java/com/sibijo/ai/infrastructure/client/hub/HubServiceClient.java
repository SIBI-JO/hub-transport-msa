package com.sibijo.ai.infrastructure.client.hub;

import com.sibijo.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.UUID;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "hub-service")
public interface HubServiceClient {

    @GetMapping("/api/hubs/{hubId}")
    ApiResponse<HubInfoDto> getHubInfo(
            @PathVariable("hubId") UUID hubId,
            @RequestHeader("Authorization") String bearerToken
    );
}

