package com.sibijo.hub_routes.infrastructure.persistence;

import com.sibijo.hub_routes.domain.model.HubRoutesEntity;
import com.sibijo.hub_routes.presentation.dto.HubRoutesResponseDto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaHubRoutesRepository extends JpaRepository<HubRoutesEntity, UUID> {

    boolean existsByDepartureIdAndDestinationId(UUID departureId, UUID destinationId);

    Optional<HubRoutesEntity> findByDepartureIdAndDestinationId(UUID startHubId, UUID endHubId);

    Optional<HubRoutesEntity> findByHashSequence(String hashSequence);
}
