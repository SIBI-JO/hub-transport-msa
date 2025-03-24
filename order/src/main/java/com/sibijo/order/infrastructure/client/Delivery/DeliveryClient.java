package com.sibijo.order.infrastructure.client.Delivery;

import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "delivery-service")
public interface DeliveryClient {

    @Retry(name = "deliveryServiceRetry")
    @PostMapping("/api/deliveries")
    void createDelivery(@RequestBody DeliveryRequestDto requestDto);
}
