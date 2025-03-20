package com.sibijo.user.infrastructure.client.company;

import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CompanyResponseDto {
    private UUID hubId; //필수
}
