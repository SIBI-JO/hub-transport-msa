package com.sibijo.hub_routes.infrastructure.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record HubServiceResponseDto(
        UUID hubId,
        String hubName,
        String hubLocation,
        BigDecimal latitude,
        BigDecimal longitude,
        String hubTypeName

) {

}
