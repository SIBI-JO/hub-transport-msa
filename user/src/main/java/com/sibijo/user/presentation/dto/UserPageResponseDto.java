package com.sibijo.user.presentation.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserPageResponseDto {
    private int page;
    private int size;
    private int total;

    private List<UserDetailsResponseDto> users;
}
