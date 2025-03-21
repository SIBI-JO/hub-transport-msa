package com.sibijo.hub_routes.infrastructure.persistence;

import com.sibijo.hub_routes.domain.model.HubRoutesEntity;
import com.sibijo.hub_routes.domain.repository.HubRoutesRepository;
import com.sibijo.hub_routes.presentation.dto.HubRoutesResponseDto;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HubRoutesRepositoryHandler implements HubRoutesRepository {

    private final JpaHubRoutesRepository jpaHubRoutesRepository;
    private final QueryDslHubRoutesRepository queryDslHubRoutesRepository;

    /**
     * @param hubRoutesId
     * @return
     */
    @Override
    public Optional<HubRoutesEntity> findById(UUID hubRoutesId) {
        return jpaHubRoutesRepository.findById(hubRoutesId);
    }

    /**
     * @param departureId
     * @param destinationId
     * @return
     */
    @Override
    public boolean existsByDepartureAndDestinationID(UUID departureId, UUID destinationId) {
        return jpaHubRoutesRepository.existsByDepartureIdAndDestinationId(departureId,
                destinationId);
    }

    /**
     * @param hubRoutesEntity
     * @return
     */
    @Override
    public HubRoutesEntity save(HubRoutesEntity hubRoutesEntity) {
        return jpaHubRoutesRepository.save(hubRoutesEntity);
    }

    /**
     * @param validatedPageable
     * @return
     */
    @Override
    public Page<HubRoutesResponseDto> searchHubRoutes(Pageable validatedPageable) {
        return queryDslHubRoutesRepository.searchHubRoutes(validatedPageable);
    }

    /**
     * @param hubRoutesEntity
     */
    @Override
    public void deleteHubRoute(HubRoutesEntity hubRoutesEntity) {
        jpaHubRoutesRepository.delete(hubRoutesEntity);
    }

    /**
     * delivery 서버
     * @param startHubId
     * @param endHubId
     * @return
     */
    @Override
    public Optional<HubRoutesEntity> findByDepartureIdAndDestinationId(UUID startHubId, UUID endHubId) {
        return jpaHubRoutesRepository.findByDepartureIdAndDestinationId(startHubId, endHubId);
    }

}
