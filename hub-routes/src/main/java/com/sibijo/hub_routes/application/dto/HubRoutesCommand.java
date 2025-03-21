package com.sibijo.hub_routes.application.dto;

import com.sibijo.hub_routes.infrastructure.dto.HubServiceClientDto;
import java.util.List;

public record HubRoutesCommand(
        HubServiceClientDto departure,
        HubServiceClientDto destination,
        List<HubServiceClientDto> centralHubList
) {

}
