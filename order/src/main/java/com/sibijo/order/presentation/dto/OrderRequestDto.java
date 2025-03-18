package com.sibijo.order.presentation.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {

    private UUID supplierId;

    private UUID recipientsId;

    private UUID productId;

    private Integer amount;

    private String request;

    // 배송에 관한 정보를 위한 수령인과 수령인의 SlackId
    private String receiver;

    private String receiverSlackId;
}
