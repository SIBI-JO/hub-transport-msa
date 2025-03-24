package com.sibijo.ai.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sibijo.ai.application.config.SlackApiProperties;
import com.sibijo.ai.domain.entity.SlackMessage;

import com.sibijo.ai.infrastructure.repository.SlackMessageRepository;
import java.nio.charset.StandardCharsets;
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
    private final SlackApiProperties slackApiProperties;

    public SlackNotificationService(RestTemplate restTemplate,
            SlackMessageRepository slackMessageRepository,
            SlackApiProperties slackApiProperties) {
        this.restTemplate = restTemplate;
        this.slackMessageRepository = slackMessageRepository;
        this.slackApiProperties = slackApiProperties;
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
        if (userSlackId == null || userSlackId.trim().isEmpty()) {
            throw new IllegalArgumentException("유효한 Slack User ID가 필요합니다.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(slackApiProperties.getApiToken());
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        Map<String, String> body = new HashMap<>();
        body.put("channel", userSlackId);
        body.put("text", message);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonBody = objectMapper.writeValueAsString(body);

            HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(slackApiProperties.getApiUrl(), request, String.class);

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
