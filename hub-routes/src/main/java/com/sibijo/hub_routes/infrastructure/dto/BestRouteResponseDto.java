package com.sibijo.hub_routes.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BestRouteResponseDto {
    private Map<Integer, List<String>> bestPathMap;
    private BigDecimal bestRouteDistance;
    private int bestRouteTime;
    private UUID departureHubId;
    private UUID destinationHubId;
}
