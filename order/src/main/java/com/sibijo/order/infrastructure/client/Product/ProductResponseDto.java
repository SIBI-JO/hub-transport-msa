package com.sibijo.order.infrastructure.client.Product;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {

    private UUID hubId;
    private Long amount;

}
