package com.sibijo.ai.application.service;

import com.sibijo.ai.infrastructure.client.GeminiApiClient;
import com.sibijo.ai.presentation.dto.OrderDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeminiNotificationService {

    private final GeminiApiClient geminiApiClient;

    public GeminiNotificationService(GeminiApiClient geminiApiClient) {
        this.geminiApiClient = geminiApiClient;
    }

    public String generateAiSlackMessage(OrderDto order) {
        // 예시 prompt 구성 – 실제 요구사항에 따라 조정 가능
        String transit = (order.getTransitCenters() == null || order.getTransitCenters().isEmpty())
                ? "없음"
                : String.join(", ", order.getTransitCenters());
        String prompt = String.format(
                "주문 번호: %s\n" +
                        "공급사: %s / %s\n" +
                        "상품 정보: %s\n" +
                        "요청 사항 (납기일자 및 시간): %s\n" +
                        "발송지: %s\n" +
                        "경유지: %s\n" +
                        "도착지: %s\n" +
                        "배송 담당자: %s / %s\n" +
                        "배송 담당자 근무시간: 09:00 - 18:00\n" +
                        "위 모든 정보를 고려하여, 납기에 맞추기 위해 최종 발송 시한을 포함한 Slack 메시지 전문을 생성해줘.",
                order.getOrderId(),
                order.getOrdererName(), order.getOrdererEmail(),
                order.getProductInfo(),
                order.getRequestInfo(),
                order.getDispatchCenter(),
                transit,
                order.getDestination(),
                order.getDeliveryPersonName(), order.getDeliveryPersonEmail()
        );
        return geminiApiClient.requestAiMessage(prompt);
    }
}
