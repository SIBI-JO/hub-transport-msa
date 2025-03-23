package com.sibijo.ai.presentation.dto;

import lombok.Data;

import java.util.UUID;

/**
 * Order -> AI 서비스로 전달되는 DTO
 */
@Data
public class AiNotificationRequestDto {
    private UUID orderId;
    private String userSlackId;
}
