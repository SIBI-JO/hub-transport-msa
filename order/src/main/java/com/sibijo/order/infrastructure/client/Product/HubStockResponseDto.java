package com.sibijo.order.infrastructure.client.Product;



import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class HubStockResponseDto {
    private UUID hubStockId;
    private UUID hubId;
    private UUID companyId;
    private UUID productId;
    private Long amount;
}

