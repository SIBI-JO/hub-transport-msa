package com.sibijo.product.client;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyResponseDto {
    private UUID hubId;
    private String deliveryAddress;
}
