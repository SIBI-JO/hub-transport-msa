package com.sibijo.order.infrastructure.client.Delivery;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRequestDto {

    private UUID orderId;

    private UUID supplierId;

    private UUID recipientsId;

    // 배송에 관한 정보를 위한 수령인과 수령인의 SlackId
    private String receiver;

    private String receiverSlackId;

    private String token;

    // 롤백 처리를 위한 정보
    private UUID productId;

    private Long productAmount;

    private Long orderAmount;
}
