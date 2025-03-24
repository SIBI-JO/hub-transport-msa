package com.sibijo.delivery.presentation.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StockInfomationDto {

    private UUID productId;   // 상품 ID

    private Long stockRollbackAmount; // 기존 재고 수량

}
