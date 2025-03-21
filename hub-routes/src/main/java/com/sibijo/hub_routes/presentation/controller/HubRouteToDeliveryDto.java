package com.sibijo.hub_routes.presentation.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HubRouteToDeliveryDto {
        private String distance;
        private String estimatedTime;
}
