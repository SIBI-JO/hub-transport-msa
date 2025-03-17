package com.sibijo.order.infrastructure.client.Delivery;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryCreateRequestDto {

    private DeliveryRequestDto deliveryRequest;  // 배송 정보
    private DeliveryRouteRequestDto routeRequest;  // 배송 경로 정보
}
