package com.sibijo.order.infrastructure.client.Company;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyResponseDto {

    private UUID hubId;

    private String deliveryAddress;

}
