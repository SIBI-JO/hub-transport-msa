package com.sibijo.hub.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record HubRequestDto(

        @NotBlank(message = "허브 이름은 필수 입력값입니다.")
        @Size(max = 20, message = "허브 이름은 최대 20자까지 입력 가능합니다.")
        String hubName,

        @NotBlank(message = "허브 주소는 필수 입력값입니다.")
        @Size(max = 50, message = "허브 주소는 최대 50자까지 입력 가능합니다.")
        String hubLocation,

        //추후 수정 필요
        @NotBlank(message = "허브 타입은 필수 입력값입니다.")
        @Size(max = 10, message = "허브 타입은 최대 10자까지 입력 가능합니다.")
        String hubTypeName
        ) {

}
