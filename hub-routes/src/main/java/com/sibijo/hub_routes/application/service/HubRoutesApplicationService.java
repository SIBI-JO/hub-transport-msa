package com.sibijo.hub_routes.application.service;

import com.sibijo.hub_routes.presentation.controller.HubRouteToDeliveryDto;
import com.sibijo.hub_routes.presentation.dto.HubRoutesRequestDto;
import com.sibijo.hub_routes.presentation.dto.HubRoutesResponseDto;
import com.sibijo.hub_routes.presentation.dto.HubRoutesUpdateRequestDto;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HubRoutesApplicationService {

    HubRoutesResponseDto createHubRoutes(HubRoutesRequestDto hubRoutesRequestDto);

    Page<HubRoutesResponseDto> searchHubRoutes(Pageable validatedPageable);

    HubRoutesResponseDto getHubRoute(UUID hubRoutesId);

    HubRoutesResponseDto updateHubRoutes(UUID hubRoutesId, HubRoutesUpdateRequestDto hubRoutesUpdateRequestDto);

    void deleteHubRoute(UUID hubRoutesId);

    HubRouteToDeliveryDto getHubRouteForOrder(UUID startHubId, UUID endHubId);
}
