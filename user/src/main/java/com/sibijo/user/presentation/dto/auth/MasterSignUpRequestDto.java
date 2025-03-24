package com.sibijo.user.presentation.dto.auth;

import com.sibijo.user.domain.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MasterSignUpRequestDto {
    @NotBlank(message = "username은 필수 입력값입니다.")
    @Size(min = 4, max = 10)
    @Pattern(regexp = "^[a-z0-9]+$")
    private String username;

    @NotBlank(message = "slackId은 필수 입력값입니다.")
    private String slackId;

    @NotBlank(message = "password는 필수 입력값입니다.")
    @Size(min = 8, max = 20)
    @Pattern(regexp = "^[A-Za-z0-9_!#$%&'*+/=?`{|}~^.-]+$")
    private String password;
    private String adminToken;

}
