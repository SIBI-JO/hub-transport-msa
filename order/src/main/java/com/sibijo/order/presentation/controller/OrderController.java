package com.sibijo.order.presentation.controller;

import com.sibijo.common.dto.ApiResponse;
import com.sibijo.common.utils.Auth.AuthUtil;
import com.sibijo.common.utils.Auth.JwtUtil;
import com.sibijo.order.application.dto.OrderResponseDto;
import com.sibijo.order.application.service.OrderService;
import com.sibijo.order.domain.entity.Order;
import com.sibijo.order.presentation.dto.OrderCreateUpdateRequestDto;
import com.sibijo.order.presentation.dto.OrderRequestDto;
import com.sibijo.order.presentation.dto.OrderUpdateRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

    private final JwtUtil jwtUtil;
    private final OrderService orderService;

    /**
     *  주문 생성
     */
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponseDto>> createOrder(HttpServletRequest request,
            @Valid @RequestBody OrderRequestDto requestDto
            ) {
        String token = jwtUtil.extractToken(request);
        return ResponseEntity.ok(ApiResponse.success("주문 생성 성공", orderService.createOrder(requestDto, token)));
    }


    /**
     *  생성 완료 전인 주문 수정
     */
    @PatchMapping("/{orderId}/update-delivery")
    public void updateOrderFromDelivery(@PathVariable UUID orderId, @RequestBody
            OrderCreateUpdateRequestDto requestDto) {
        orderService.updateOrderWithDelivery(orderId, requestDto);
    }


    /**
     *   주문 전체 조회
     *   권한 : Hub_Manager -> 자신의 허브만, Delivery_Manager/Company_Manager -> 본인의 주문만
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderResponseDto>>> getOrders(
            HttpServletRequest request,
            @PageableDefault(page = 1, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        String token = jwtUtil.extractToken(request);
        return ResponseEntity.ok(ApiResponse.success("주문 전체 조회 성공", orderService.getOrders(token, pageable)));
    }

    /**
     *   주문 상세 조회
     *   권한 : Hub_Manager -> 자신의 허브만, Delivery_Manager/Company_Manager -> 본인의 주문만
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponseDto>> getOrderById(@PathVariable UUID orderId,
            HttpServletRequest request) {
        String token = jwtUtil.extractToken(request);
        return ResponseEntity.ok(ApiResponse.success("주문 상세 조회 성공", orderService.getOrderById(orderId, token)));
    }


    /**
     *   주문 수정
     *   권한 : Hub_Manager -> 자신의 허브만 , Master -> All
     */
    @PutMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponseDto>> updateOrder(@PathVariable UUID orderId,
            @RequestBody OrderUpdateRequestDto requestDto,
            HttpServletRequest request) {
        String token = jwtUtil.extractToken(request);
        return ResponseEntity.ok(ApiResponse.success("주문 수정 성공", orderService.updateOrder(orderId, requestDto, token)));
    }

    /**
     *   주문 삭제
     *   권한 : Hub_Manager -> 자신의 허브만 , Master -> All
     */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponseDto>> deleteOrder(
            @PathVariable UUID orderId,
            HttpServletRequest request) {
        String token = jwtUtil.extractToken(request);
        return ResponseEntity.ok(ApiResponse.success("주문 삭제 성공", orderService.deleteOrder(orderId, token)));
    }

    /**
     *  배송 생성 실패 시, 임시 생성된 주문을 취소
     */
    @DeleteMapping("/internal/{orderId}")
    public void deleteOrderInternal(@PathVariable UUID orderId) {
        orderService.deleteOrderInternal(orderId);
    }
}
