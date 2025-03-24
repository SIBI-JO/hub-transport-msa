package com.sibijo.ai.application.service;

import com.sibijo.ai.infrastructure.client.GeminiApiClient;
import com.sibijo.ai.infrastructure.client.delivery.DeliveryRouteResponseDto;
import com.sibijo.ai.presentation.dto.OrderDto;
import org.springframework.stereotype.Service;

@Service
public class GeminiNotificationService {

    private final GeminiApiClient geminiApiClient;

    public GeminiNotificationService(GeminiApiClient geminiApiClient) {
        this.geminiApiClient = geminiApiClient;
    }


    /**
     * 주문 및 배송경로 정보를 활용하여, 실제 거리와 실제 소요시간 정보를 포함하고,
     * 요청 시간에서 실제 소요시간을 뺀 최종 발송 시한을 산출하여 AI에 전달합니다.
     */
    public String generateAiSlackMessage(OrderDto order, DeliveryRouteResponseDto routeResponseDto) {
        // 실제 거리 처리: realDistance 값이 있으면 "실제 거리: XX KM"로, 없으면 "정보 없음"
        String realDistanceStr = (routeResponseDto != null && routeResponseDto.getRealDistance() != null)
                ? routeResponseDto.getRealDistance() + " KM"
                : "정보 없음";

        // 실제 소요시간 처리: realDuration이 문자열(분 단위)로 제공된다면 파싱하여 사용하고, 없으면 기본 60분 사용
        int realDurationMinutes = 0; // 기본값
        if (routeResponseDto != null && routeResponseDto.getRealDuration() != null) {
            try {
                realDurationMinutes = Integer.parseInt(routeResponseDto.getRealDuration());
            } catch (NumberFormatException e) {
                // 파싱 실패 시 기본값 60분 사용
            }
        }
        String realDurationStr = realDurationMinutes + " 분";

        // 최종 발송 시한 계산: 요청 정보(requestInfo)에서 날짜/시간 정보를 추출한 후, 실제 소요시간만큼 차감
        String finalShippingDeadline = ShippingDeadlineCalculator.computeFinalShippingDeadline(order.getRequestInfo(), realDurationMinutes);

        String prompt = String.format(
                "주문 번호: %s\n" +
                        "주문자 정보: %s\n" +
                        "상품 정보: %s\n" +
                        "요청 사항: %s\n" +
                        "발송지: %s\n" +
                        "경유지: %s\n" +
                        "도착지: %s\n" +
                        "배송 담당자: %s\n" +
                        "배송 담당자 근무시간: 09:00 - 18:00\n" +
                        "실제 거리: %s\n" +
                        "실제 소요시간: %s\n" +
                        "최종 발송 시한: %s\n" +
                        "이 정보를 바탕으로 Slack 메시지 전문을 생성해줘.",
                order.getOrderId(),
                order.getOrdererName(),
                order.getProductInfo(),
                order.getRequestInfo(),
                order.getDispatchCenter(),
                order.getTransitCenters() != null ? String.join(", ", order.getTransitCenters()) : "없음",
                order.getDestination(),
                order.getDeliveryPersonName(),
                realDistanceStr,
                realDurationStr,
                finalShippingDeadline
        );
        return geminiApiClient.requestAiMessage(prompt);
    }
}
