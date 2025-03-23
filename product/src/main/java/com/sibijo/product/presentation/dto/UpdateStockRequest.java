package com.sibijo.product.presentation.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStockRequest {
    private Long newAmount;
}
