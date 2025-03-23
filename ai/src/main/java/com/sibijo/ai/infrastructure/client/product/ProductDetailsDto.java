package com.sibijo.ai.infrastructure.client.product;

import lombok.Data;
import java.util.UUID;

@Data
public class ProductDetailsDto {
    private UUID productId;
    private String productName;
    private Integer price;
}