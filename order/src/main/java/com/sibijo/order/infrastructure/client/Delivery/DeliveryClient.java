package com.sibijo.order.infrastructure.client.Delivery;

import com.sibijo.order.presentation.dto.StockInfomationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "delivery-service")
public interface DeliveryClient {

//    @PostMapping("/api/deliveries")
//    DeliveryResponseDto createDelivery(@RequestBody DeliveryRequestDto requestDto);

    @PostMapping("/api/deliveries")
    void createDelivery(@RequestBody DeliveryRequestDto requestDto, @RequestBody StockInfomationDto stockInfomationDto);

}
