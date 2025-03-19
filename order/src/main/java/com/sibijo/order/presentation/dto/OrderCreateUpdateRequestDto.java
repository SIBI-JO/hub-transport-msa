package com.sibijo.order.presentation.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateUpdateRequestDto {

    private UUID deliveryId;

    private UUID supplierHubId;

    private UUID recipientHubId;


}