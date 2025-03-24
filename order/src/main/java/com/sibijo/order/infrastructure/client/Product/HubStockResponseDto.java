package com.sibijo.order.infrastructure.client.Product;


import lombok.*;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HubStockResponseDto {
    private UUID hubStockId;
    private UUID hubId;
    private UUID companyId;
    private UUID productId;
    private Long amount;
}

