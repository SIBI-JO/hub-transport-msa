package com.sibijo.hub_routes.presentation.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class HubRoutesResponseDto {
    private UUID id;
    private UUID departureId;
    private UUID destinationId;
    private BigDecimal distance;
    private Integer estimatedTime;
    private String sequence;
    private String hashedSequence;

}


