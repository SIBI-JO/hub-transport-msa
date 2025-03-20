package com.sibijo.delivery.presentation.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRouteRequestDto {

    private Integer sequence;
    private UUID startHubId;
    private UUID endHubId;
    private String expectedDistance;
    private String expectedTime;

}
