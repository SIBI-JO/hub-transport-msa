package com.sibijo.user.presentation.dto.user;

import com.sibijo.user.domain.enums.Role;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserSearchDetailsReponseDto {
    private Long userId;
    private String username;
    private String slackId;
    private Role role;
    private Boolean isDeleted;

}
