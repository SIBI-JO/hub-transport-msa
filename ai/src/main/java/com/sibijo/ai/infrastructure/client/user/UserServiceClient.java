package com.sibijo.ai.infrastructure.client.user;

import com.sibijo.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.UUID;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/api/users/hub-manager/{hubId}")
    ApiResponse<HubManagerDto> getHubManagerByHubId(
            @PathVariable("hubId") UUID hubId,
            @RequestHeader("Authorization") String bearerToken
    );
}
