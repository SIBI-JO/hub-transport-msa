package com.sibijo.hub_routes.presentation.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record HubRoutesResponseDto(
        UUID id,
        UUID departureId,
        UUID destinationID,
        UUID centralId,
        BigDecimal distance,
        Integer estimatedTime
) {

}
