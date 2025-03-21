package com.sibijo.hub_routes.infrastructure.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HubServiceClientDto {

    private UUID hubId;
    private String hubName;
    private String hubLocation;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String hubTypeName;
}
