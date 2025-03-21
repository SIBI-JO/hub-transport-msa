package com.sibijo.hub_routes.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CentralHubDto(
        UUID departureNearCentralId,
        UUID destinationNearCentralId,
        BigDecimal departureAndCentralDistance,
        BigDecimal destinationAndCentralDistance,
        Integer departureAndCentralDuration,
        Integer destinationAndCentralDuration

) {

}
