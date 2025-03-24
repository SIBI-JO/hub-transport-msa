package com.sibijo.order.presentation.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderSearchDto {
    private Long ordererId;
    private UUID supplierId;
    private UUID recipientsId;
}
