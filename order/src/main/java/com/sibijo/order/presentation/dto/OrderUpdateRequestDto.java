package com.sibijo.order.presentation.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdateRequestDto {

    private UUID supplierId;

    private UUID recipientsId;

    private UUID productId;

    private Integer amount;

    private String request;


}
