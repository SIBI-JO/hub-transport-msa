package com.sibijo.ai.infrastructure.client;

import com.sibijo.ai.infrastructure.config.FeignClientConfig;
import com.sibijo.common.dto.ApiResponse;
import com.sibijo.ai.presentation.dto.OrderServiceResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.UUID;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * AI -> Order 서비스 FeignClient
 */
@FeignClient(name = "order-service", configuration = FeignClientConfig.class)
public interface OrderServiceClient {

    @GetMapping("/api/orders/{orderId}")
    ApiResponse<OrderServiceResponseDto> getOrderById(
            @PathVariable("orderId") UUID orderId,
            @RequestHeader("Authorization") String bearerToken
    );
}

