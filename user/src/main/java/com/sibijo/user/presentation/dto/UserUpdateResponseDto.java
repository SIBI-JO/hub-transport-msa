package com.sibijo.user.presentation.dto;

import com.sibijo.user.domain.enums.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserUpdateResponseDto {
    private Long userId;
    private String username;
    private String email;
    private Role role;
}