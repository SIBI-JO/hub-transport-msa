package com.sibijo.ai.presentation.controller;

import com.sibijo.ai.application.service.GeminiNotificationService;
import com.sibijo.ai.application.service.SlackNotificationService;
import com.sibijo.ai.infrastructure.client.order.OrderServiceClient;
import com.sibijo.ai.infrastructure.client.order.OrderServiceResponseDto;
import com.sibijo.ai.infrastructure.client.product.ProductServiceClient;
import com.sibijo.ai.infrastructure.client.product.ProductDetailsDto;
import com.sibijo.ai.infrastructure.client.delivery.DeliveryServiceClient;
import com.sibijo.ai.infrastructure.client.delivery.DeliveryDetailsDto;
import com.sibijo.ai.infrastructure.client.hub.HubServiceClient;
import com.sibijo.ai.infrastructure.client.hub.HubInfoDto;
import com.sibijo.ai.presentation.dto.AiNotificationRequestDto;
import com.sibijo.ai.presentation.dto.OrderDto;
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
    private final OrderServiceClient orderServiceClient;
    private final ProductServiceClient productServiceClient;
    private final DeliveryServiceClient deliveryServiceClient;
    private final HubServiceClient hubServiceClient;

    /**
     * Order 서비스에서 주문 생성 알림을 받는 엔드포인트
     */
    @PostMapping("/dm")
    public ResponseEntity<?> handleOrderCreated(@RequestBody AiNotificationRequestDto dto,
            @RequestHeader("Authorization") String bearerToken) {
        System.out.println("Received bearerToken: " + bearerToken);
        try {
            // 1) Order 서비스에서 주문 상세 조회 (Bearer 접두사 포함)
            ApiResponse<OrderServiceResponseDto> orderResponse = orderServiceClient.getOrderById(dto.getOrderId(), "Bearer " + bearerToken);
            if (!orderResponse.getStatus().equalsIgnoreCase("SUCCESS") || orderResponse.getData() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found or invalid response");
            }
            OrderServiceResponseDto orderData = orderResponse.getData();
            System.out.println("Order 조회 완료");

            // 2) 추가 정보 조회: 상품 상세 정보
            ApiResponse<ProductDetailsDto> productResponse = productServiceClient.getProductDetails(orderData.getProductId(), "Bearer " + bearerToken);
            ProductDetailsDto productDetails = productResponse.getData();
            System.out.println("상품 정보 조회 완료");

            // 3) 추가 정보 조회: 배송 상세 정보 (발송지/도착지, 경유지 등)
            ApiResponse<DeliveryDetailsDto> deliveryResponse = deliveryServiceClient.getDeliveryDetails(orderData.getDeliveryId(), "Bearer " + bearerToken);
            DeliveryDetailsDto deliveryDetails = deliveryResponse.getData();
            System.out.println("배송 정보 조회 완료");
            System.out.println(orderData.getDeliveryId());

            System.out.println(deliveryDetails.getDeliveryId());
            System.out.println(deliveryDetails.getStartHubId());
            System.out.println(deliveryDetails.getEndHubId());

            UUID departureHubId = deliveryDetails.getStartHubId();
            UUID destinationHubId = deliveryDetails.getEndHubId();
            if (departureHubId == null || destinationHubId == null) {
                throw new IllegalStateException("배송 상세 정보에 허브 ID가 누락되었습니다.");
            }

            // 4) 허브 정보 조회: 발송지와 도착지 허브의 이름
            ApiResponse<HubInfoDto> departureHubResponse = hubServiceClient.getHubInfo(departureHubId, "Bearer " + bearerToken);
            HubInfoDto departureHub = departureHubResponse.getData();

            ApiResponse<HubInfoDto> destinationHubResponse = hubServiceClient.getHubInfo(destinationHubId, "Bearer " + bearerToken);
            HubInfoDto destinationHub = destinationHubResponse.getData();
            System.out.println("허브 정보 조회 완료");


            // 5) 모든 정보를 합쳐서 AI 메시지 생성에 사용할 DTO 구성
            OrderDto orderDto = new OrderDto();
            orderDto.setOrderId(orderData.getOrderId().toString());
            orderDto.setOrdererName("공급사 ID: " + orderData.getSupplierId());
            orderDto.setOrdererEmail("unknown@example.com"); // 실제 이메일은 별도 조회 필요
            orderDto.setProductInfo("상품명: " + productDetails.getProductName() +
                    " / 수량: " + orderData.getAmount());
            orderDto.setRequestInfo(orderData.getRequest());
            orderDto.setDispatchCenter(departureHub.getHubName());
            orderDto.setTransitCenters(null); // 경유지가 있다면 해당 리스트로 세팅
            orderDto.setDestination(destinationHub.getHubName());
            orderDto.setDeliveryPersonName("홍길동");
            orderDto.setDeliveryPersonEmail("delivery@company.com");
            System.out.println("모든 정보 구성 완료");

            // 6) Gemini API 호출하여 AI 메시지 생성 (최종 발송 시한 포함)
            String aiMessage = geminiNotificationService.generateAiSlackMessage(orderDto);
            System.out.println("AI 메시지 생성 완료");

            // 7) Slack DM 전송
            slackNotificationService.sendDirectMessageToUser(dto.getUserSlackId(), aiMessage);
            System.out.println("Slack DM 전송 완료");

            return ResponseEntity.ok(Collections.singletonMap("aiMessage", aiMessage));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("AI 메시지 생성/전송 중 오류: " + e.getMessage());
        }
    }
}
