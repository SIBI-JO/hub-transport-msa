package com.sibijo.order.infrastructure.client.Delivery;

import com.sibijo.common.dto.ApiResponse;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "delivery-service")
public interface DeliveryClient {

    @Retry(name = "deliveryServiceRetry")
    @PostMapping("/api/deliveries")
    ApiResponse<UUID> createDelivery(@RequestBody DeliveryRequestDto requestDto);

}
