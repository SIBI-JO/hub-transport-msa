package com.sibijo.ai.application.service;

import com.sibijo.ai.domain.entity.SlackMessage;
import com.sibijo.ai.infrastructure.repository.SlackMessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class SlackNotificationService {

    private final RestTemplate restTemplate;
    private final SlackMessageRepository slackMessageRepository;

    @Value("${slack.api.url}")
    private String slackApiUrl; // 예: https://slack.com/api/chat.postMessage

    @Value("${slack.api.token}")
    private String slackApiToken; // Slack Bot User OAuth Token

    public SlackNotificationService(RestTemplate restTemplate,
            SlackMessageRepository slackMessageRepository) {
        this.restTemplate = restTemplate;
        this.slackMessageRepository = slackMessageRepository;
    }

    /**
     * 특정 채널(또는 사용자)로 Slack 메시지를 전송.
     * (기존 channel 기반)
     */
    public void sendSlackMessage(String channel, String message) {
        // ... (기존 채널 전송 코드와 동일)
    }

    /**
     * 특정 사용자(Slack User ID)에게 DM을 전송하고, DB에 메시지를 저장합니다.
     *
     * @param userSlackId 예) U08XXXXXX 형태의 Slack User ID
     * @param message     전송할 메시지 내용
     */
    public void sendDirectMessageToUser(String userSlackId, String message) {
        // 1) Slack API 호출 준비
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(slackApiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // DM을 보낼 때 channel 필드에 "사용자 ID"를 직접 입력
        Map<String, String> body = new HashMap<>();
        body.put("channel", userSlackId); // "U08XXXXXX" 같은 실제 Slack User ID
        body.put("text", message);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response =
                restTemplate.postForEntity(slackApiUrl, request, String.class);

        // 2) HTTP 응답 코드 확인
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("슬랙 메시지 전송 실패(HTTP): " + response.getStatusCode());
        }

        // 3) Slack 응답 바디에서 "ok" 여부 확인
        String responseBody = response.getBody();
        if (responseBody == null) {
            throw new RuntimeException("슬랙 메시지 전송 실패: 응답 바디가 없습니다.");
        }
        System.out.println("Slack response body: " + responseBody);

        if (!responseBody.contains("\"ok\":true")) {
            throw new RuntimeException("슬랙 메시지 전송 실패(Slack 응답): " + responseBody);
        }

        // 4) 전송 성공 시, DB에 메시지 저장
        SlackMessage slackMessage = new SlackMessage();
        slackMessage.setRecipientSlackId(userSlackId);
        slackMessage.setMessage(message);
        slackMessage.setSentAt(LocalDateTime.now());
        slackMessageRepository.save(slackMessage);
    }
}
