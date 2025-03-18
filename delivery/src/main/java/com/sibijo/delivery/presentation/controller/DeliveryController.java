package com.sibijo.delivery.presentation.controller;

import com.sibijo.delivery.application.service.DeliveryServiceCenter;
import com.sibijo.delivery.presentation.dto.OrderToDeliveryRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j(topic = "배송 Controller")
@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryServiceCenter deliveryService;

    @PostMapping
    public void createDelivery(
            @RequestBody OrderToDeliveryRequestDto requestDto,
            @RequestHeader(value = "X-User-Id", required = true) String userId,
            @RequestHeader(value = "X-Role", required = true) String role) {

        deliveryService.createDelivery(requestDto, userId);
//        return ResponseEntity.ok();
    }


}
