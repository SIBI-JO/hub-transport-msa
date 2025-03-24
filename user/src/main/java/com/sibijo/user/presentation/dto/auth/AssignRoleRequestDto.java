package com.sibijo.user.presentation.dto.auth;

import com.sibijo.user.domain.enums.DeliveryType;
import com.sibijo.user.domain.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignRoleRequestDto {

    @NotBlank(message = "유저 이름은 필수 입력값입니다.")
    private String username;

    @NotNull(message = "역할(role)은 필수 입력값입니다.")
    private Role role;

    private UUID hubId;       // HUB, DELIVERY 역할에서 사용
    private UUID companyId;   // COMPANY 역할에서 사용
    private DeliveryType deliveryType; // DELIVERY 역할에서 사용

}
