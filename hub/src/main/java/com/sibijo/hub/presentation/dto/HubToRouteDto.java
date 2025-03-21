package com.sibijo.hub.presentation.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HubToRouteDto {

    private UUID hubId;
    private String hubName;
    private String hubLocation;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String hubTypeName;

}
