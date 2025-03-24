package com.sibijo.ai.infrastructure.client.delivery;

import com.sibijo.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import java.util.UUID;

@FeignClient(name = "delivery-service", contextId = "deliveryServiceClient")
public interface DeliveryRouteClient {

    @GetMapping("/api/deliveries/routes/delivery/{deliveryId}")
    ApiResponse<DeliveryRouteResponseDto> getRouteByDeliveryId(
            @PathVariable("deliveryId") UUID deliveryId,
            @RequestHeader("Authorization") String bearerToken
    );
}
