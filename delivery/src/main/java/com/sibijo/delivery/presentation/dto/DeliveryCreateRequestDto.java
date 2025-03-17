package com.sibijo.delivery.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DeliveryCreateRequestDto {

    private DeliveryRequestDto deliveryRequest;  // 배송 정보
    private DeliveryRouteRequestDto routeRequest;  // 배송 경로 정보

}
