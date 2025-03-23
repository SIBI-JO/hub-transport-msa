package com.sibijo.ai.infrastructure.client.delivery;

import com.sibijo.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.UUID;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "delivery-service")
public interface DeliveryServiceClient {

    @GetMapping("/api/deliveries/{deliveryId}")
    ApiResponse<DeliveryDetailsDto> getDeliveryDetails(
            @PathVariable("deliveryId") UUID deliveryId,
            @RequestHeader("Authorization") String bearerToken
    );
}

