package com.sibijo.ai.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sibijo.ai.domain.entity.SlackMessage;
import com.sibijo.ai.infrastructure.repository.SlackMessageRepository;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
     * 특정 사용자(Slack User ID)에게 DM을 전송하고, DB에 메시지를 저장합니다.
     * 반환값으로 생성된 UUID messageId를 리턴합니다.
     *
     * @param userSlackId 예) U08XXXXXX 형태의 Slack User ID
     * @param message     전송할 메시지 내용
     * @return 생성된 메시지의 UUID messageId
     */
    public UUID sendDirectMessageToUser(String userSlackId, String message) {
        System.out.println(userSlackId);
        // userSlackId 유효성 검사
        if (userSlackId == null || userSlackId.trim().isEmpty()) {
            throw new IllegalArgumentException("유효한 Slack User ID가 필요합니다.");
        }


        // 1) Slack API 호출 준비
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(slackApiToken);
        // 명시적으로 charset 정보를 포함한 Content-Type 설정
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        // 요청 본문 구성
        Map<String, String> body = new HashMap<>();
        body.put("channel", userSlackId);
        body.put("text", message);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonBody = objectMapper.writeValueAsString(body);

            HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(slackApiUrl, request, String.class);

            // 2) HTTP 응답 코드 및 응답 바디 확인
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("슬랙 메시지 전송 실패(HTTP): " + response.getStatusCode());
            }

            String responseBody = response.getBody();
            if (responseBody == null) {
                throw new RuntimeException("슬랙 메시지 전송 실패: 응답 바디가 없습니다.");
            }
            System.out.println("Slack response body: " + responseBody);

            if (!responseBody.contains("\"ok\":true")) {
                throw new RuntimeException("슬랙 메시지 전송 실패(Slack 응답): " + responseBody);
            }

            // 3) 메시지 저장 전 UUID 생성 및 할당
            UUID messageUuid = UUID.randomUUID();

            SlackMessage slackMessage = new SlackMessage();
            slackMessage.setMessageId(messageUuid);
            slackMessage.setRecipientSlackId(userSlackId);
            slackMessage.setMessage(message);
            slackMessage.setSentAt(LocalDateTime.now());
            slackMessageRepository.save(slackMessage);

            return messageUuid;
        } catch (Exception e) {
            throw new RuntimeException("Slack 메시지 전송 중 예외 발생: " + e.getMessage(), e);
        }
    }
}
