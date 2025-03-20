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

    /**
     * 주문 정보를 기반으로 Gemini API에 보낼 프롬프트를 생성하고, AI 메시지를 반환합니다.
     */
    public String generateAiSlackMessage(OrderDto order) {
        String prompt = buildPrompt(order);
        String aiMessage = geminiApiClient.requestAiMessage(prompt);
        return aiMessage;
    }

    /**
     * 주문 정보를 바탕으로 Gemini API에 전달할 prompt 문자열을 구성합니다.
     */
    private String buildPrompt(OrderDto order) {
        // 경유지 리스트를 콤마로 연결
        List<String> centers = order.getTransitCenters();
        String transitCenters = (centers == null || centers.isEmpty()) ? "" : String.join(", ", centers);

        return String.format(
                "주문 번호: %s\n" +
                        "주문자 정보: %s / %s\n" +
                        "상품 정보: %s\n" +
                        "요청 사항: %s\n" +
                        "발송지: %s\n" +
                        "경유지: %s\n" +
                        "도착지: %s\n" +
                        "배송 담당자: %s / %s\n" +
                        "배송 담당자 근무시간: 09:00 - 18:00\n" +
                        "위 정보를 종합하여, 납기에 맞추기 위해 '최종 발송 시한'을 포함한 Slack 메시지 전문을 생성해줘.",
                order.getOrderId(),
                order.getOrdererName(), order.getOrdererEmail(),
                order.getProductInfo(),
                order.getRequestInfo(),
                order.getDispatchCenter(),
                transitCenters,
                order.getDestination(),
                order.getDeliveryPersonName(), order.getDeliveryPersonEmail()
        );
    }
}
