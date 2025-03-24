package com.sibijo.hub_routes.presentation.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record
HubRoutesResponseDto(
        UUID id,
        UUID departureId,
        UUID destinationID,
        BigDecimal distance,
        Integer estimatedTime,
        String sequence,
        String hashedSequence
) {

}
