package com.sibijo.order.presentation.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StockInfomationDto {

    private UUID productId;   // 상품 ID

    private Long stockRollbackAmount; // 재고 수량

}
