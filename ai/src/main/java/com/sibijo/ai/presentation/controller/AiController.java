package com.sibijo.ai.presentation.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.sibijo.ai.application.service.GeminiNotificationService;
import com.sibijo.ai.application.service.SlackNotificationService;
import com.sibijo.ai.presentation.dto.OrderDto;
import com.sibijo.ai.presentation.dto.OrderServiceResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Controller;
import java.util.Collections;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final GeminiNotificationService geminiNotificationService;
    private final SlackNotificationService slackNotificationService;
    private final RestTemplate restTemplate;

    @Value("${order.service.url}")
    private String orderServiceUrl; // 예: http://localhost:8081/api/orders

    @Value("${order.service.useDummyData:false}")
    private boolean useDummyData;

    // Slack 전송 시 사용할 채널 (예: "#dispatch-channel")
    @Value("${slack.channel}")
    private String slackChannel;

    public AiController(GeminiNotificationService geminiNotificationService,
            SlackNotificationService slackNotificationService,
            RestTemplate restTemplate) {
        this.geminiNotificationService = geminiNotificationService;
        this.slackNotificationService = slackNotificationService;
        this.restTemplate = restTemplate;
    }

    /**
     * 주문 정보를 기반으로 AI 메시지를 생성하고, Slack으로 전송합니다.
     */
    @GetMapping("/orders/{orderId}/dm")
    public ResponseEntity<?> generateAndSendDmToUser(
            @PathVariable("orderId") Long orderId,
            @RequestParam("userSlackId") String userSlackId
    ) {
        try {
            // 1) 주문 상세정보 조회
            OrderServiceResponseDto orderServiceData = fetchOrderFromOrderService(orderId);
            if (orderServiceData == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
            }
            // 2) OrderDto 변환
            OrderDto orderDto = mapToOrderDto(orderServiceData);
            // 3) AI 메시지 생성
            String aiMessage = geminiNotificationService.generateAiSlackMessage(orderDto);
            // 4) Slack DM 전송 (userSlackId에 전송)
            slackNotificationService.sendDirectMessageToUser(userSlackId, aiMessage);

            // 결과 반환
            return ResponseEntity.ok(Collections.singletonMap("aiMessage", aiMessage));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("AI 메시지 생성 및 Slack DM 전송 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * Order 마이크로서비스의 /api/orders/{orderId} 엔드포인트를 호출하여 주문 상세정보를 가져옵니다.
     * 더미 데이터 사용 플래그가 활성화되어 있으면 임의의 데이터를 반환합니다.
     */
    private OrderServiceResponseDto fetchOrderFromOrderService(Long orderId) {
        if (useDummyData) {
            return generateDummyOrder(orderId);
        }

        String url = orderServiceUrl + "/" + orderId;
        HttpHeaders headers = new HttpHeaders();
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
     * 더미 주문 데이터를 생성합니다.
     */
    private OrderServiceResponseDto generateDummyOrder(Long orderId) {
        OrderServiceResponseDto dummy = new OrderServiceResponseDto();
        dummy.setOrderId(orderId);
        dummy.setSupplierName("더미 주문자");
        dummy.setRecipientsName("더미 수령자");
        dummy.setProductName("테스트 상품");
        dummy.setAmount(1);
        dummy.setRequest("테스트 요청 내용");
        return dummy;
    }

    /**
     * OrderServiceResponseDto를 AI 메시지 생성을 위한 OrderDto로 매핑합니다.
     */
    private OrderDto mapToOrderDto(OrderServiceResponseDto src) {
        OrderDto target = new OrderDto();
        target.setOrderId(src.getOrderId().toString());
        // supplierName을 주문자 정보로 사용 (필요시 실제 필드에 맞게 수정)
        target.setOrdererName(src.getSupplierName());
        target.setOrdererEmail("unknown@example.com"); // 추가 정보가 없으면 기본값 사용
        target.setProductInfo(String.format("%s %d개", src.getProductName(), src.getAmount()));
        target.setRequestInfo(src.getRequest());
        target.setDispatchCenter("경기 북부 센터"); // 예시 값, 실제값은 Order 서비스에서 제공
        target.setTransitCenters(null); // 필요 시 경유지 리스트 채워주기
        target.setDestination("부산시 사하구 낙동대로 1번길 1 해산물월드");
        target.setDeliveryPersonName("고길동");
        target.setDeliveryPersonEmail("kdk@sparta.world");
        return target;
    }
}
