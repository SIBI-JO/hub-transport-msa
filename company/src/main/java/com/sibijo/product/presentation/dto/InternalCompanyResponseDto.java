package com.sibijo.product.presentation.dto;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternalCompanyResponseDto {
    private UUID hubId;
}