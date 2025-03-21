package com.sibijo.hub_routes.domain.repository;

import com.sibijo.hub_routes.domain.model.HubRoutesEntity;
import com.sibijo.hub_routes.presentation.dto.HubRoutesResponseDto;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HubRoutesRepository {

    Optional<HubRoutesEntity> findById(UUID hubRoutesId);

    boolean existsByDepartureAndDestinationID(UUID departureId, UUID destinationId);

    HubRoutesEntity save(HubRoutesEntity hubRoutesEntity);

    Page<HubRoutesResponseDto> searchHubRoutes(Pageable validatedPageable);

    void deleteHubRoute(HubRoutesEntity hubRoutesEntity);

    Optional<HubRoutesEntity> findByDepartureIdAndDestinationId(UUID startHubId, UUID endHubId);
}
