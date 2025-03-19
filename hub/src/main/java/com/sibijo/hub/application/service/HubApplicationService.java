package com.sibijo.hub.application.service;

import com.sibijo.hub.presentation.dto.HubRequestDto;
import com.sibijo.hub.presentation.dto.HubResponseDto;
import com.sibijo.hub.presentation.dto.HubUpdateRequestDto;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HubApplicationService {

    Page<HubResponseDto> searchHubs(
            String hubName,
            String hubLocation,
            String hubTypeName,
            Pageable pageable);

    HubResponseDto createHub(@Valid HubRequestDto hubRequestDto);

    HubResponseDto getHub(UUID hubId);

    HubResponseDto updateHub(UUID hubId, HubUpdateRequestDto hubUpdateRequestDto);

    void deleteHub(UUID hubId);

    boolean isHubExists(UUID hubId);
}
