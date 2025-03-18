package com.sibijo.ai.presentation.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.sibijo.ai.application.service.GeminiNotificationService;
import com.sibijo.ai.presentation.dto.OrderDto;
import com.sibijo.ai.presentation.dto.OrderServiceResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * AI 마이크로서비스 컨트롤러
 * - Order 마이크로서비스로부터 주문 정보를 조회하고, AI 메시지를 생성합니다.
 */
@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final GeminiNotificationService geminiNotificationService;
    private final RestTemplate restTemplate;

    @Value("${order.service.url}")
    private String orderServiceUrl; // 예: http://localhost:8081/api/orders

    public AiController(GeminiNotificationService geminiNotificationService,
            RestTemplate restTemplate) {
        this.geminiNotificationService = geminiNotificationService;
        this.restTemplate = restTemplate;
    }

    /**
     * /api/ai/orders/{orderId}/slack-message
     * - Order 마이크로서비스에서 주문 상세정보를 조회한 후, AI 메시지를 생성합니다.
     */
    @GetMapping("/orders/{orderId}/slack-message")
    public ResponseEntity<?> generateSlackMessageFromOrder(
            @PathVariable("orderId") Long orderId,
            @RequestHeader("Authorization") String bearerToken
    ) {
        try {
            // 1. Order 마이크로서비스에서 주문 상세정보 조회
            OrderServiceResponseDto orderServiceData = fetchOrderFromOrderService(orderId, bearerToken);
            if (orderServiceData == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
            }
            // 2. 조회한 데이터를 AI 메시지 생성용 OrderDto로 매핑
            OrderDto orderDto = mapToOrderDto(orderServiceData);
            // 3. Gemini API를 호출해 AI 메시지 생성
            String aiMessage = geminiNotificationService.generateAiSlackMessage(orderDto);
            // 4. 결과 반환
            return ResponseEntity.ok(Collections.singletonMap("aiMessage", aiMessage));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("AI 메시지 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * Order 마이크로서비스의 /api/orders/{orderId} 엔드포인트를 호출하여 주문 상세정보를 가져옵니다.
     */
    private OrderServiceResponseDto fetchOrderFromOrderService(Long orderId, String bearerToken) {
        String url = orderServiceUrl + "/" + orderId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", bearerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, JsonNode.class);
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            JsonNode body = response.getBody();
            JsonNode dataNode = body.get("data");
            if (dataNode == null || dataNode.isNull()) {
                return null;
            }
            OrderServiceResponseDto dto = new OrderServiceResponseDto();
            dto.setOrderId(dataNode.get("orderId").asLong());
            dto.setSupplierName(dataNode.get("supplierName").asText(""));
            dto.setRecipientsName(dataNode.get("recipientsName").asText(""));
            dto.setProductName(dataNode.get("productName").asText(""));
            dto.setAmount(dataNode.get("amount").asInt(0));
            dto.setRequest(dataNode.get("request").asText(""));
            return dto;
        }
        return null;
    }

    /**
     * OrderServiceResponseDto를 AI 메시지 생성을 위한 OrderDto로 매핑합니다.
     * (필요에 따라 실제 필드 및 로직은 수정)
     */
    private OrderDto mapToOrderDto(OrderServiceResponseDto src) {
        OrderDto target = new OrderDto();
        target.setOrderId(src.getOrderId().toString());
        // 여기서는 supplierName을 주문자 정보로 사용 (실제 상황에 맞게 조정)
        target.setOrdererName(src.getSupplierName());
        target.setOrdererEmail("unknown@example.com"); // 추가 정보가 없으면 기본값
        target.setProductInfo(String.format("%s %d개", src.getProductName(), src.getAmount()));
        target.setRequestInfo(src.getRequest());
        target.setDispatchCenter("경기 북부 센터"); // 예시 값, 실제값은 Order 서비스나 기타 서비스에서 제공
        target.setTransitCenters(null); // 필요시 경유지 리스트 채워주기
        target.setDestination("부산시 사하구 낙동대로 1번길 1 해산물월드");
        target.setDeliveryPersonName("고길동");
        target.setDeliveryPersonEmail("kdk@sparta.world");
        return target;
    }
}
