package com.sibijo.ai.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class SlackNotificationService {

    private final RestTemplate restTemplate;

    @Value("${slack.api.url}")
    private String slackApiUrl; // 예: https://slack.com/api/chat.postMessage

    @Value("${slack.api.token}")
    private String slackApiToken; // Slack Bot User OAuth Token

    public SlackNotificationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 지정한 채널로 Slack 메시지를 전송합니다.
     *
     * @param channel Slack 채널 (예: "#dispatch-channel")
     * @param message 전송할 메시지 내용
     */
    public void sendSlackMessage(String channel, String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(slackApiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("channel", channel);
        body.put("text", message);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(slackApiUrl, request, String.class);

        // 1) HTTP Status가 2xx인지 확인
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("슬랙 메시지 전송 실패(HTTP): " + response.getStatusCode());
        }

        // 2) Slack 응답 바디(JSON)를 파싱하여 ok 필드를 확인
        String responseBody = response.getBody();
        if (responseBody == null) {
            throw new RuntimeException("슬랙 메시지 전송 실패: 응답 바디가 없습니다.");
        }
        System.out.println("Slack response body: " + responseBody);

        // 간단히 contains로 체크 가능(정식으론 JSON 파싱 라이브러리 사용 권장)
        if (!responseBody.contains("\"ok\":true")) {
            throw new RuntimeException("슬랙 메시지 전송 실패(Slack 응답): " + responseBody);
        }
    }

}
