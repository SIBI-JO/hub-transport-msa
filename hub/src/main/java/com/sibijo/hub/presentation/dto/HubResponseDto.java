package com.sibijo.hub.presentation.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record HubResponseDto(
        UUID hubId,
        String hubName,
        String hubLocation,
        BigDecimal latitude,
        BigDecimal longitude,
        String hubType
) {
}
