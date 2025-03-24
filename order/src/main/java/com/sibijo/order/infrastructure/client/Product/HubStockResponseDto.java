package com.sibijo.order.infrastructure.client.Product;


import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HubStockResponseDto {
    private UUID hubStockId;
    private UUID hubId;
    private UUID companyId;
    private UUID productId;
    private Long amount;
}

