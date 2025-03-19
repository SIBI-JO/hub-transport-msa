package com.sibijo.hub_routes.infrastructure.persistence;

import com.sibijo.hub_routes.domain.model.HubRoutesEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaHubRoutesRepository extends JpaRepository<HubRoutesEntity, UUID> {

    boolean existsByDepartureIdAndDestinationId(UUID departureId, UUID destinationId);
}
