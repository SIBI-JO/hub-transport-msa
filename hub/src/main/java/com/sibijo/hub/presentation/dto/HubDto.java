package com.sibijo.hub.presentation.dto;

import com.sibijo.hub.domain.model.HubEntity;
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

    public static HubDto fromEntity(HubEntity hubEntity) {
        return HubDto.builder()
                .hubId(hubEntity.getId())
                .hubName(hubEntity.getHubName())
                .hubLocation(hubEntity.getHubLocation())
                .latitude(hubEntity.getLatitude())
                .longitude(hubEntity.getLongitude())
                .hubTypeName(hubEntity.getHubType().getHubTypeName())
                .build();
    }
}
