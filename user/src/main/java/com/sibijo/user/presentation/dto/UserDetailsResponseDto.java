package com.sibijo.user.presentation.dto;

import com.sibijo.user.domain.enums.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDetailsResponseDto {
    private Long userId;
    private String username;
    private String slackId;
    private Role role;
}