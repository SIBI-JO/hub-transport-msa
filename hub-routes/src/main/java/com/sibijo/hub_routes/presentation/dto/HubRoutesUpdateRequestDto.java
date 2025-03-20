package com.sibijo.hub_routes.presentation.dto;

import java.util.UUID;

public record HubRoutesUpdateRequestDto(
        UUID departureId,
        UUID destinationId
) {

}
