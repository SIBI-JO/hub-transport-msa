package com.sibijo.order.infrastructure.client.Delivery;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRequestDto {

    private UUID startHubId;   // 출발 허브 ID
    private UUID endHubId;     // 도착 허브 ID
    private String deliveryAddress; // 배송 주소
    private String receiver;   // 수령인
    private String receiverSlackId; // 수령인의 Slack ID
    private UUID deliveryManagerId; // 배송 담당자 ID

}
