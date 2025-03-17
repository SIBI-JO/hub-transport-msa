// UpdateStockRequest DTO
package com.sibijo.product.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStockRequest {
    private Long newAmount;
}
