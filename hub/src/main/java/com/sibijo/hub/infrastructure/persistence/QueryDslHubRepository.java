package com.sibijo.hub.infrastructure.persistence;

import com.sibijo.hub.domain.model.HubType;
import com.sibijo.hub.presentation.dto.HubResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QueryDslHubRepository {

    Page<HubResponseDto> searchHubs(String hubName, String hubLocation, HubType hubType,
            Pageable pageable);
}
