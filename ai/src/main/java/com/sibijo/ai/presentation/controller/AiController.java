package com.sibijo.ai.presentation.controller;

import com.sibijo.ai.application.service.GeminiNotificationService;
import com.sibijo.ai.application.service.SlackNotificationService;
import com.sibijo.ai.domain.entity.SlackMessage;
import com.sibijo.ai.infrastructure.client.delivery.DeliveryRouteClient;
import com.sibijo.ai.infrastructure.client.delivery.DeliveryRouteResponseDto;
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
import java.util.List;
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
    private final DeliveryAgentServiceClient deliveryAgentServiceClient;
    private final DeliveryRouteClient deliveryRouteClient; // 배송경로 조회용
    private final SlackMessageRepository slackMessageRepository;

    /**
     * 주문 생성 시, 배송경로 정보를 포함하여 AI 메시지를 생성하고 Slack DM으로 전송합니다.
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

            // 4) 출발지 및 도착지 허브 정보 조회
            ApiResponse<HubInfoDto> hubResponse = hubServiceClient.getHubInfo(departureHubId, "Bearer " + bearerToken);
            HubInfoDto departureHub = hubResponse.getData();
            UUID destinationHubId = deliveryDetails.getEndHubId();
            if (destinationHubId == null) {
                throw new IllegalStateException("배송 상세 정보에 도착 허브 ID가 누락되었습니다.");
            }
            ApiResponse<HubInfoDto> destinationHubResponse = hubServiceClient.getHubInfo(destinationHubId, "Bearer " + bearerToken);
            HubInfoDto destinationHub = destinationHubResponse.getData();

            // 5) 허브 담당자 및 배송담당자 정보 조회
            ApiResponse<HubManagerDto> hubManagerResponse = userServiceClient.getHubManagerByHubId(departureHubId, "Bearer " + bearerToken);
            HubManagerDto hubManager = hubManagerResponse.getData();
            if (hubManager == null) {
                throw new IllegalStateException("허브 담당자 정보가 조회되지 않았습니다.");
            }
            Long deliveryManagerId = deliveryDetails.getDeliveryManagerId();
            ApiResponse<DeliveryAgentDetailsResponseDto> agentResponse =
                    deliveryAgentServiceClient.getDeliveryAgentById(deliveryManagerId, "Bearer " + bearerToken);
            DeliveryAgentDetailsResponseDto deliveryAgent = agentResponse.getData();
            if (deliveryAgent == null) {
                throw new IllegalStateException("배송담당자 정보가 조회되지 않았습니다.");
            }

            System.out.println(orderData.getDeliveryId());
            // 6) 배송경로 정보 조회 (deliveryId 기준)
            ApiResponse<DeliveryRouteResponseDto> routeResponse = deliveryRouteClient.getRouteByDeliveryId(orderData.getDeliveryId(), "Bearer " + bearerToken);
            DeliveryRouteResponseDto routeDetails = routeResponse.getData();

            // 7) 주문 정보 DTO 구성
            OrderDto orderDto = new OrderDto();
            orderDto.setOrderId(orderData.getOrderId().toString());
            orderDto.setOrdererName("공급사 ID: " + orderData.getSupplierId());
            orderDto.setOrdererEmail("unknown@example.com");
            orderDto.setProductInfo("상품명: " + productDetails.getProductName() +
                    " / 수량: " + orderData.getAmount());
            orderDto.setRequestInfo(orderData.getRequest());
            orderDto.setDispatchCenter(departureHub.getHubName());
            orderDto.setTransitCenters(routeDetails != null ? List.of(departureHub.getHubName(), destinationHub.getHubName()) : null);
            orderDto.setDestination(destinationHub.getHubName());
            orderDto.setDeliveryPersonName(deliveryAgent.getUsername());
            orderDto.setDeliveryPersonEmail(deliveryAgent.getSlackId());

            // 8) Gemini API 호출하여 AI 메시지 생성 (배송경로의 예상거리 및 예상 소요시간 활용)
            String aiMessage = geminiNotificationService.generateAiSlackMessage(orderDto, routeDetails);

            // 9) Slack DM 전송
            slackNotificationService.sendDirectMessageToUser(hubManager.getSlackId(), aiMessage);
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
