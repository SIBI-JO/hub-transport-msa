package com.sibijo.product.infrastructure.client;

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
