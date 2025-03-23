package com.sibijo.order.infrastructure.client.Product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStockRequest {

    private Long newAmount;

}
