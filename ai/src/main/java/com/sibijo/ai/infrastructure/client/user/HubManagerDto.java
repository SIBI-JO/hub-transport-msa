package com.sibijo.ai.infrastructure.client.user;

import lombok.Data;
import java.util.UUID;

@Data
public class HubManagerDto {
    private UUID userId;
    private String name;
    private String email;
    private String slackUserId;  // Slack 메시지 전송에 필요한 필드
}
