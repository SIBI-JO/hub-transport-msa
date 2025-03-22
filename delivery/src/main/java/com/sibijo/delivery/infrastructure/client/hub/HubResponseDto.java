package com.sibijo.delivery.infrastructure.client.hub;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HubResponseDto {

    private String distance;

    private String estimatedTime;

}