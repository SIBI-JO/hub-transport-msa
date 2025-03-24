package com.sibijo.ai.infrastructure.client;


import com.sibijo.ai.application.config.GeminiApiProperties;
import com.sibijo.ai.presentation.dto.GeminiApiResponseDto;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class GeminiApiClient {

    private final GeminiApiProperties geminiApiProperties;
    private final RestTemplate restTemplate;

    public GeminiApiClient(GeminiApiProperties geminiApiProperties, RestTemplate restTemplate) {
        this.geminiApiProperties = geminiApiProperties;
        this.restTemplate = restTemplate;
    }

    /**
     * Gemini API에 요청을 보내고, AI 메시지를 반환합니다.
     */
    public String requestAiMessage(String prompt) {
        String apiUrl = geminiApiProperties.getApiUrl();
        String apiKey = geminiApiProperties.getApiKey();
        System.out.println("Gemini API URL: " + apiUrl); // 디버그 출력
        if (apiUrl == null || apiUrl.trim().isEmpty()) {
            throw new IllegalStateException("Gemini API URL이 설정되지 않았습니다.");
        }
        String urlWithKey = apiUrl + "?key=" + apiKey;


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
