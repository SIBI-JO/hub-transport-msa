package com.sibijo.hub_routes.presentation.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record HubRoutesRequestDto(
        @NotNull(message = "출발 허브 아이디는 필수 입력값입니다.")
        UUID departureId,

        @NotNull(message = "도착 허브 아이디는 필수 입력값입니다.")
        UUID destinationId


) {

}
