package com.sibijo.hub_routes.application.service;

import com.sibijo.hub_routes.presentation.dto.HubRouteToDeliveryDto;
import com.sibijo.hub_routes.presentation.dto.HubRoutesRequestDto;
import com.sibijo.hub_routes.presentation.dto.HubRoutesResponseDto;
import com.sibijo.hub_routes.presentation.dto.HubRoutesUpdateRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface HubRoutesApplicationService {

    HubRoutesResponseDto createHubRoutes(String token, HubRoutesRequestDto hubRoutesRequestDto);

    Page<HubRoutesResponseDto> searchHubRoutes(String token, Pageable validatedPageable);

    HubRoutesResponseDto getHubRoute(String token, UUID hubRoutesId);

    HubRoutesResponseDto updateHubRoutes(String token, UUID hubRoutesId, HubRoutesUpdateRequestDto hubRoutesUpdateRequestDto);

    void deleteHubRoute(String token, UUID hubRoutesId);

    HubRouteToDeliveryDto getHubRouteForOrder(UUID startHubId, UUID endHubId);
}
