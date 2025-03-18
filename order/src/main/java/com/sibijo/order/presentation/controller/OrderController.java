package com.sibijo.order.presentation.controller;

import com.sibijo.order.application.service.OrderService;
import com.sibijo.order.presentation.dto.OrderRequestDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j(topic = "주문 Controller")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

//    @GetMapping
//    public ResponseEntity<?> getOrders(
//            @RequestHeader(value = "X-User-Id", required = true) String userId,
//            @RequestHeader(value = "X-Role", required = true) String role,
//            Pageable pageable) {
//
//
//        Page<OrderResponseDto> orders = orderService.getOrders(userId, pageable);
//
//        return ResponseEntity.ok(new ApiResponseDTO<>("sucess", orders));
//    }

    @PostMapping
    public void createOrder(
            @RequestBody OrderRequestDto requestDto,
            @RequestHeader(value = "X-User-Id", required = true) String userId,
            @RequestHeader(value = "X-Role", required = true) String role) {

        orderService.createOrder(requestDto, userId);
//        return ResponseEntity.ok();
    }


    @PutMapping("/{orderId}/update-delivery")
    public void updateOrderFromDelivery(@PathVariable UUID orderId, @RequestParam UUID deliveryId) {
        orderService.updateOrderWithDelivery(orderId, deliveryId);
    }

}
