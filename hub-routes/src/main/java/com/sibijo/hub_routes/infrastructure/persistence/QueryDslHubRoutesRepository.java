package com.sibijo.hub_routes.infrastructure.persistence;

import com.sibijo.hub_routes.presentation.dto.HubRoutesResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QueryDslHubRoutesRepository {

    Page<HubRoutesResponseDto> searchHubRoutes(Pageable validatedPageable);
}
