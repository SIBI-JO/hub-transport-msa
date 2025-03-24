package com.sibijo.ai.presentation.controller;

import com.sibijo.ai.application.service.GeminiNotificationService;
import com.sibijo.ai.application.service.SlackNotificationService;
import com.sibijo.ai.domain.entity.SlackMessage;
import com.sibijo.ai.infrastructure.client.order.OrderServiceClient;
import com.sibijo.ai.infrastructure.client.order.OrderServiceResponseDto;
import com.sibijo.ai.infrastructure.client.product.ProductServiceClient;
import com.sibijo.ai.infrastructure.client.product.ProductDetailsDto;
import com.sibijo.ai.infrastructure.client.delivery.DeliveryServiceClient;
import com.sibijo.ai.infrastructure.client.delivery.DeliveryDetailsDto;
import com.sibijo.ai.infrastructure.client.hub.HubServiceClient;
import com.sibijo.ai.infrastructure.client.hub.HubInfoDto;
import com.sibijo.ai.infrastructure.client.user.DeliveryAgentDetailsResponseDto;
import com.sibijo.ai.infrastructure.client.user.HubManagerDto;
import com.sibijo.ai.infrastructure.client.user.UserServiceClient;
import com.sibijo.ai.infrastructure.client.user.DeliveryAgentServiceClient;
import com.sibijo.ai.infrastructure.repository.SlackMessageRepository;
import com.sibijo.ai.presentation.dto.AiNotificationRequestDto;
import com.sibijo.ai.presentation.dto.OrderDto;
import com.sibijo.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final GeminiNotificationService geminiNotificationService;
    private final SlackNotificationService slackNotificationService;
    private final OrderServiceClient orderServiceClient;
    private final ProductServiceClient productServiceClient;
    private final DeliveryServiceClient deliveryServiceClient;
    private final HubServiceClient hubServiceClient;
    private final UserServiceClient userServiceClient;
    private final DeliveryAgentServiceClient deliveryAgentServiceClient; // 배송담당자 정보 조회용
    private final SlackMessageRepository slackMessageRepository;

    /**
     * 주문 생성 알림: 주문 생성 시, 출발지 허브 담당자와 배송담당자 정보를 반영하여 AI 메시지를 생성하고 Slack DM 전송
     */
    @PostMapping("/orders/dm")
    public ResponseEntity<?> handleOrderCreated(@RequestBody AiNotificationRequestDto dto,
            @RequestHeader("Authorization") String bearerToken) {
        try {
            // 1) 주문 상세 조회
            ApiResponse<OrderServiceResponseDto> orderResponse = orderServiceClient.getOrderById(dto.getOrderId(), "Bearer " + bearerToken);
            if (!orderResponse.getStatus().equalsIgnoreCase("SUCCESS") || orderResponse.getData() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Order not found or invalid response");
            }
            OrderServiceResponseDto orderData = orderResponse.getData();

            // 2) 상품 상세 조회
            ApiResponse<ProductDetailsDto> productResponse = productServiceClient.getProductDetails(orderData.getProductId(), "Bearer " + bearerToken);
            ProductDetailsDto productDetails = productResponse.getData();

            // 3) 배송 상세 정보 조회
            ApiResponse<DeliveryDetailsDto> deliveryResponse = deliveryServiceClient.getDeliveryDetails(orderData.getDeliveryId(), "Bearer " + bearerToken);
            DeliveryDetailsDto deliveryDetails = deliveryResponse.getData();
            UUID departureHubId = deliveryDetails.getStartHubId();
            if (departureHubId == null) {
                throw new IllegalStateException("배송 상세 정보에 출발 허브 ID가 누락되었습니다.");
            }

            // 4) 출발지 허브 정보 조회
            ApiResponse<HubInfoDto> hubResponse = hubServiceClient.getHubInfo(departureHubId, "Bearer " + bearerToken);
            HubInfoDto departureHub = hubResponse.getData();

            // 5) 출발지 허브 담당자 정보 조회 (User Service)
            ApiResponse<HubManagerDto> hubManagerResponse = userServiceClient.getHubManagerByHubId(departureHubId, "Bearer " + bearerToken);
            HubManagerDto hubManager = hubManagerResponse.getData();
            if (hubManager == null) {
                throw new IllegalStateException("허브 담당자 정보가 조회되지 않았습니다.");
            }

            // 6) 배송담당자 정보 조회 (Delivery 서비스의 DeliveryDetailsDto에서 deliveryManagerId 추출)
            Long deliveryManagerId = deliveryDetails.getDeliveryManagerId();
            ApiResponse<DeliveryAgentDetailsResponseDto> agentResponse =
                    deliveryAgentServiceClient.getDeliveryAgentById(deliveryManagerId, "Bearer " + bearerToken);
            DeliveryAgentDetailsResponseDto deliveryAgent = agentResponse.getData();
            if (deliveryAgent == null) {
                throw new IllegalStateException("배송담당자 정보가 조회되지 않았습니다.");
            }

            // 7) AI 메시지 생성을 위한 주문 정보 DTO 구성
            OrderDto orderDto = new OrderDto();
            orderDto.setOrderId(orderData.getOrderId().toString());
            orderDto.setOrdererName("공급사 ID: " + orderData.getSupplierId());
            orderDto.setOrdererEmail("unknown@example.com");
            orderDto.setProductInfo("상품명: " + productDetails.getProductName() +
                    " / 수량: " + orderData.getAmount());
            orderDto.setRequestInfo(orderData.getRequest());
            orderDto.setDispatchCenter(departureHub.getHubName());
            orderDto.setTransitCenters(null); // 경유지가 있을 경우 리스트 세팅
            orderDto.setDestination("미정");  // 필요에 따라 도착지 정보 설정

            // 실제 배송담당자의 이름과 Slack ID 반영 (예: 이름은 name, Slack ID는 slackUserId 필드)
            orderDto.setDeliveryPersonName(deliveryAgent.getName());
            orderDto.setDeliveryPersonEmail(deliveryAgent.getSlackUserId());

            // 8) Gemini API 호출하여 AI 메시지 생성
            String aiMessage = geminiNotificationService.generateAiSlackMessage(orderDto);

            // 9) 출발지 허브 담당자의 Slack User ID를 사용하여 DM 전송
            slackNotificationService.sendDirectMessageToUser(hubManager.getSlackUserId(), aiMessage);
            System.out.println("Slack DM 전송 완료");

            return ResponseEntity.ok(Collections.singletonMap("aiMessage", aiMessage));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("AI 메시지 생성/전송 중 오류: " + e.getMessage());
        }
    }

    // 단건 조회: GET /api/ai/messages/{messageId}
    @GetMapping("/messages/{messageId}")
    public ResponseEntity<ApiResponse<SlackMessage>> getMessage(@PathVariable UUID messageId) {
        Optional<SlackMessage> messageOpt = slackMessageRepository.findById(messageId);
        if (messageOpt.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success("AI 메시지 조회 성공", messageOpt.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.exception("AI 메시지를 찾을 수 없습니다.", null));
        }
    }

    // 전체 조회: GET /api/ai/messages
    @GetMapping("/messages")
    public ResponseEntity<ApiResponse<java.util.List<SlackMessage>>> getAllMessages() {
        java.util.List<SlackMessage> messages = slackMessageRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("전체 Slack 메시지 조회 성공", messages));
    }
}
