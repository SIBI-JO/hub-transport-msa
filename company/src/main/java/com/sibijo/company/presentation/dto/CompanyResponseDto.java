package com.sibijo.company.presentation.dto;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyResponseDto {
    private UUID hubId;
    private String deliveryAddress;
}
