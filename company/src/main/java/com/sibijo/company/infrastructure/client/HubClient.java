package com.sibijo.company.infrastructure.client;

import com.sibijo.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.UUID;

@FeignClient(name = "hub-service")
public interface HubClient {

    @GetMapping("/api/hubs/{hubId}/exists")
    ApiResponse<Boolean> hubExists(@PathVariable("hubId") UUID hubId);
}
