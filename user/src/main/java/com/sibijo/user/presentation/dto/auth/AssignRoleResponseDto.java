package com.sibijo.user.presentation.dto.auth;

import com.sibijo.user.domain.enums.DeliveryType;
import com.sibijo.user.domain.enums.Role;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AssignRoleResponseDto {
    private Long userId;
    private Role role;
    private UUID hubId;
    private UUID companyId;
    private DeliveryType deliveryType;
    private Integer deliveryNumber;
}
