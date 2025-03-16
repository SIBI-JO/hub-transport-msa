package com.sibijo.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

    private String productName;
    private Integer price;
    private Long companyId;
    private Long hubId;
}
