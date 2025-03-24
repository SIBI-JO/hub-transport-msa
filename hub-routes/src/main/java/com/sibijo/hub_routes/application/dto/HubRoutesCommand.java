package com.sibijo.hub_routes.application.dto;

import com.sibijo.hub_routes.infrastructure.dto.HubDto;

import java.util.List;
import java.util.UUID;

public record HubRoutesCommand(
        UUID departure,
        UUID destination,
        List<HubDto> hubDtoList
) {

}
