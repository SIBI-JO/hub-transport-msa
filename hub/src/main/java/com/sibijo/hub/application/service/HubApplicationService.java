package com.sibijo.hub.application.service;

import com.sibijo.hub.presentation.dto.HubRequestDto;
import com.sibijo.hub.presentation.dto.HubResponseDto;
import com.sibijo.hub.presentation.dto.HubToRouteDto;
import com.sibijo.hub.presentation.dto.HubUpdateRequestDto;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HubApplicationService {

    Page<HubResponseDto> searchHubs(
            String token,
            String hubName,
            String hubLocation,
            String hubTypeName,
            Pageable pageable);

    HubResponseDto createHub(String token, @Valid HubRequestDto hubRequestDto);

    HubResponseDto getHub(String token, UUID hubId);

    HubResponseDto updateHub(String token, UUID hubId, HubUpdateRequestDto hubUpdateRequestDto);

    void deleteHub(String token, UUID hubId);

    boolean isHubExists(UUID hubId);

    HubToRouteDto getHubForHubRoutes(UUID hubId);
}
