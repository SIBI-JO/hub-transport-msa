package com.sibijo.product.infrastructure.client;

import com.sibijo.product.presentation.dto.GeminiApiResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class GeminiApiClient {

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final RestTemplate restTemplate;

    public GeminiApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Gemini API에 요청을 보내고, AI 메시지를 반환합니다.
     */
    public String requestAiMessage(String prompt) {
        // API 키를 URL에 쿼리 파라미터로 추가
        String urlWithKey = geminiApiUrl + "?key=" + geminiApiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Authorization 헤더는 사용하지 않습니다.

        // 요청 본문을 API가 요구하는 형식으로 구성합니다.
        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);

        Map<String, Object> promptObj = new HashMap<>();
        promptObj.put("parts", Collections.singletonList(part));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", Collections.singletonList(promptObj));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<GeminiApiResponseDto> response = restTemplate.postForEntity(
                urlWithKey, request, GeminiApiResponseDto.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            String generatedText = response.getBody().getResponseText();
            if (generatedText != null) {
                return generatedText;
            } else {
                throw new RuntimeException("Gemini API 호출 성공하였으나, 생성된 메시지가 없습니다.");
            }
        } else {
            throw new RuntimeException("Gemini API 호출 실패: " + response.getStatusCode());
        }
    }
}
