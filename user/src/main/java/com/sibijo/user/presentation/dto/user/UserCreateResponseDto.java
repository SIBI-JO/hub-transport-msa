package com.sibijo.user.presentation.dto.user;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserCreateResponseDto {
    private Long userId;
}
