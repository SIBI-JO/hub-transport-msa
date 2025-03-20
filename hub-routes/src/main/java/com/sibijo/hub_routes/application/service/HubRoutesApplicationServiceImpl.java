package com.sibijo.hub_routes.application.service;

import com.sibijo.hub_routes.domain.model.HubRoutesEntity;
import com.sibijo.hub_routes.domain.repository.HubRoutesRepository;
import com.sibijo.hub_routes.domain.service.HubRoutesDomainService;
import com.sibijo.hub_routes.presentation.dto.HubRoutesRequestDto;
import com.sibijo.hub_routes.presentation.dto.HubRoutesResponseDto;
import com.sibijo.hub_routes.presentation.dto.HubRoutesUpdateRequestDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HubRoutesApplicationServiceImpl implements HubRoutesApplicationService {

    private final HubRoutesRepository hubRoutesRepository;
    private final HubRoutesDomainService hubRoutesDomainService;


    /**
     * @param hubRoutesRequestDto
     * @return
     */
    @Override
    @Transactional
    public HubRoutesResponseDto createHubRoutes(HubRoutesRequestDto hubRoutesRequestDto) {

        HubRoutesEntity hubRoutesEntity = hubRoutesDomainService.createHubRoutes(
                hubRoutesRequestDto);

        HubRoutesEntity savedHubRoutesEntity = hubRoutesRepository.save(hubRoutesEntity);
        return convertToHubRoutesResponseDto(savedHubRoutesEntity);
    }

    /**
     * @param validatedPageable
     * @return
     */
    @Override
    public Page<HubRoutesResponseDto> searchHubRoutes(Pageable validatedPageable) {
        return hubRoutesRepository.searchHubRoutes(validatedPageable);
    }

    /**
     * @param hubRoutesId
     * @return
     */
    @Override
    public HubRoutesResponseDto getHubRoute(UUID hubRoutesId) {
        return convertToHubRoutesResponseDto(hubRoutesDomainService.getHubRoute(hubRoutesId));
    }

    /**
     * @param hubRoutesId
     * @param hubRoutesUpdateRequestDto
     * @return
     */
    @Override
    @Transactional
    public HubRoutesResponseDto updateHubRoutes(UUID hubRoutesId,
            HubRoutesUpdateRequestDto hubRoutesUpdateRequestDto) {
        HubRoutesEntity hubRoutesEntity = hubRoutesDomainService.updateHubRoutes(hubRoutesId,
                hubRoutesUpdateRequestDto);
        return convertToHubRoutesResponseDto(hubRoutesEntity);
    }

    /**
     * @param hubRoutesId
     */
    @Override
    @Transactional
    public void deleteHubRoute(UUID hubRoutesId) {
        hubRoutesRepository.deleteHubRoute(hubRoutesDomainService.deleteHubRoute(hubRoutesId));
    }

    private static HubRoutesResponseDto convertToHubRoutesResponseDto(
            HubRoutesEntity hubRoutesEntity) {
        return new HubRoutesResponseDto(
                hubRoutesEntity.getId(),
                hubRoutesEntity.getDepartureId(),
                hubRoutesEntity.getDestinationId(),
                hubRoutesEntity.getCentralId(),
                hubRoutesEntity.getDistance(),
                hubRoutesEntity.getEstimatedTime()
        );
    }
}
