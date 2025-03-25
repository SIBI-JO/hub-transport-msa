package com.sibijo.hub_routes.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HubDto {
    private UUID hubId;
    private String hubName;
    private String hubLocation;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String hubTypeName;

    @Override
    public String toString() {
        return hubName;
    }
}
