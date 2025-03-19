package com.sibijo.hub_routes.domain.service;

import com.sibijo.hub_routes.domain.model.HubRoutesEntity;
import com.sibijo.hub_routes.presentation.dto.HubRoutesRequestDto;
import com.sibijo.hub_routes.presentation.dto.HubRoutesUpdateRequestDto;
import java.util.UUID;

public interface HubRoutesDomainService {

    HubRoutesEntity createHubRoutes(HubRoutesRequestDto hubRoutesRequestDto);

    HubRoutesEntity getHubRoute(UUID hubRoutesId);

    HubRoutesEntity updateHubRoutes(UUID hubRoutesId, HubRoutesUpdateRequestDto hubRoutesUpdateRequestDto);

    HubRoutesEntity deleteHubRoute(UUID hubRoutesId);
}
