package com.sibijo.user.application.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class HubResponseDto {
    private UUID hubId;
    private boolean hubStatus;
}
