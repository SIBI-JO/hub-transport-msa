package com.sibijo.product.presentation.dto;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HubStockResponse {
    private UUID hubStockId;
    private UUID hubId;
    private UUID companyId;
    private UUID productId;
    private Long amount;
}
