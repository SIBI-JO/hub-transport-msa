package com.sibijo.hub.domain.service;

import com.sibijo.hub.presentation.dto.HubRequestDto;
import com.sibijo.hub.presentation.dto.HubUpdateRequestDto;
import com.sibijo.hub.domain.model.HubEntity;
import java.util.UUID;

public interface HubDomainService {

    HubEntity createHubEntity(HubRequestDto hubRequestDto);

    HubEntity updateHubEntity(UUID hubId, HubUpdateRequestDto hubUpdateRequestDto);
}
