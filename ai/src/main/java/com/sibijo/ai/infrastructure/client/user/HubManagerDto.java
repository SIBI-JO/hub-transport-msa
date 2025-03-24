package com.sibijo.ai.infrastructure.client.user;

import lombok.Data;
import java.util.UUID;

@Data
public class HubManagerDto {
    private Long userId;
    private String username;
    private String email;
    private String slackId;  // Slack 메시지 전송에 필요한 필드
}
