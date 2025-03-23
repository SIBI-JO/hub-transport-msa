package com.sibijo.ai.presentation.controller;

import com.sibijo.ai.application.service.GeminiNotificationService;
import com.sibijo.ai.application.service.SlackNotificationService;
import com.sibijo.ai.infrastructure.client.OrderServiceClient;
import com.sibijo.ai.presentation.dto.AiNotificationRequestDto;
import com.sibijo.ai.presentation.dto.OrderDto;
import com.sibijo.ai.presentation.dto.OrderServiceResponseDto;
import com.sibijo.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.UUID;

@RestController
@RequestMapping("/api/ai/orders")
@RequiredArgsConstructor
public class AiController {

    private final GeminiNotificationService geminiNotificationService;
    private final SlackNotificationService slackNotificationService;
    private final OrderServiceClient orderServiceClient; // Order 서비스 FeignClient

    /**
     * Order 서비스에서 주문 생성 알림을 받는 엔드포인트
     */
    @PostMapping("/dm")
    public ResponseEntity<?> handleOrderCreated(@RequestBody AiNotificationRequestDto dto,
            @RequestHeader("Authorization") String bearerToken) {
        System.out.println("Received bearerToken: " + bearerToken);
        try {
            // 1) Order 서비스로부터 주문 상세 조회
            ApiResponse<OrderServiceResponseDto> response = orderServiceClient.getOrderById(dto.getOrderId(), "Bearer " + bearerToken);
            if (!response.getStatus().equalsIgnoreCase("SUCCESS") || response.getData() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found or invalid response");
            }
            System.out.println("1");

            OrderServiceResponseDto orderData = response.getData();

            // 2) AI 메시지 생성용 DTO 변환
            OrderDto orderDto = mapToOrderDto(orderData);
            System.out.println("2");

            // 3) Gemini API 호출하여 AI 메시지 생성
            String aiMessage = geminiNotificationService.generateAiSlackMessage(orderDto);

            System.out.println("3");
            // 4) Slack DM 전송
            slackNotificationService.sendDirectMessageToUser(dto.getUserSlackId(), aiMessage);

            System.out.println("4");
            return ResponseEntity.ok(Collections.singletonMap("aiMessage", aiMessage));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("AI 메시지 생성/전송 중 오류: " + e.getMessage());
        }
    }

    /**
     * OrderServiceResponseDto -> OrderDto 변환 로직 (예시)
     */
    private OrderDto mapToOrderDto(OrderServiceResponseDto src) {
        OrderDto dto = new OrderDto();
        dto.setOrderId(src.getOrderId().toString());
        dto.setOrdererName("공급사: " + src.getSupplierId());
        dto.setOrdererEmail("unknown@example.com"); // 실제 이메일 정보가 필요하면 별도 로직
        dto.setProductInfo("상품ID: " + src.getProductId() + " / 수량: " + src.getAmount());
        dto.setRequestInfo(src.getRequest());
        dto.setDispatchCenter("공급사 측 물류센터(예시)");
        dto.setTransitCenters(null); // 필요 시 경유지 리스트 설정
        dto.setDestination("수령사 측 주소(예시)");
        dto.setDeliveryPersonName("홍길동");
        dto.setDeliveryPersonEmail("delivery@company.com");
        return dto;
    }
}
