package com.sibijo.delivery.infrastructure.client.order;

import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "order-service")
public interface OrderClient {

    @PutMapping("/api/orders/{orderId}/update-delivery")
    void updateOrderWithDelivery(@PathVariable UUID orderId, @RequestBody OrderCreateUpdateRequestDto requestDto);

}
