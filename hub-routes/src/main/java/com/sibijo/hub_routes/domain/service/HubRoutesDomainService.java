package com.sibijo.hub_routes.domain.service;

import com.sibijo.hub_routes.application.dto.HubRoutesCommand;
import com.sibijo.hub_routes.domain.model.HubRoutesEntity;
import java.util.UUID;

public interface HubRoutesDomainService {

    HubRoutesEntity createHubRoutes(HubRoutesCommand hubRoutesCommand);

    HubRoutesEntity getHubRoute(UUID hubRoutesId);

    HubRoutesEntity updateHubRoutes(UUID hubRoutesId, HubRoutesCommand hubRoutesUpdateRequestDto);

    HubRoutesEntity deleteHubRoute(UUID hubRoutesId);

    HubRoutesEntity getHubRouteForOrder(UUID startHubId, UUID endHubId);
}
