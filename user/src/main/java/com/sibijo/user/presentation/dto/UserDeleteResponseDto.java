package com.sibijo.user.presentation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDeleteResponseDto {
    private Long userId;
}
