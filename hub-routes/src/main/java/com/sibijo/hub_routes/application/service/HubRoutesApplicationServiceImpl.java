package com.sibijo.hub_routes.application.service;

import com.sibijo.common.exception.CustomException;
import com.sibijo.hub_routes.application.dto.HubRoutesCommand;
import com.sibijo.hub_routes.domain.exception.HubRoutesDomainExceptionCode;
import com.sibijo.hub_routes.domain.model.HubRoutesEntity;
import com.sibijo.hub_routes.domain.repository.HubRoutesRepository;
import com.sibijo.hub_routes.domain.service.HubRoutesDomainService;
import com.sibijo.hub_routes.infrastructure.client.HubServiceClient;
import com.sibijo.hub_routes.infrastructure.dto.HubServiceClientDto;
import com.sibijo.hub_routes.presentation.controller.HubRouteToDeliveryDto;
import com.sibijo.hub_routes.presentation.dto.HubRoutesRequestDto;
import com.sibijo.hub_routes.presentation.dto.HubRoutesResponseDto;
import com.sibijo.hub_routes.presentation.dto.HubRoutesUpdateRequestDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HubRoutesApplicationServiceImpl implements HubRoutesApplicationService {

    private final HubRoutesRepository hubRoutesRepository;
    private final HubRoutesDomainService hubRoutesDomainService;
    private final HubServiceClient hubServiceClient;
    //    private final OrderServiceClient orderServiceClient;
    //    private final CompanyServiceClient companyServiceClient;

    /**
     * @param hubRoutesRequestDto
     * @return
     */
    @Override
    @Transactional
    public HubRoutesResponseDto createHubRoutes(HubRoutesRequestDto hubRoutesRequestDto) {
        //중복 체크
        if (hubRoutesRepository.existsByDepartureAndDestinationID(hubRoutesRequestDto.departureId(),
                hubRoutesRequestDto.destinationId())) {
            throw new CustomException(HubRoutesDomainExceptionCode.HUB_ROUTES_IS_DUPLICATED);
        }
        log.info("hubRoutesRequestDto ={}", hubRoutesRequestDto);
        // 허브서비스에 허브아이디로 좌표 조회
        HubServiceClientDto departure = hubServiceClient.getHubForHubRoutes(
                hubRoutesRequestDto.departureId());
        HubServiceClientDto destination = hubServiceClient.getHubForHubRoutes(
                hubRoutesRequestDto.destinationId());
        log.info("HubServiceClientDto ={}", departure);
        log.info("HubServiceClientDto ={}", destination);
        // 허브서비스에 중앙허브 데이터 전부 조회
//        List<HubServiceResponseDto> centralHubList = hubServiceClient.getCentralHub();

        // 출발, 도착 좌표, 중앙허브 데이터 -> command로 바꿈
        HubRoutesCommand hubRoutesCommand = new HubRoutesCommand(
                departure,
                destination,
                null
        );
        log.info("createHubRoutesCommand ={}", hubRoutesCommand);

        HubRoutesEntity hubRoutesEntity = hubRoutesDomainService.createHubRoutes(
                hubRoutesCommand);

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

        log.info("HubRoutesUpdateRequestDto ={}", hubRoutesUpdateRequestDto);
        // 허브서비스에 허브아이디로 좌표 조회
        HubServiceClientDto departure = null;
        if (hubRoutesUpdateRequestDto.departureId() != null) {
            departure = hubServiceClient.getHubForHubRoutes(
                    hubRoutesUpdateRequestDto.departureId());
        }
        HubServiceClientDto destination = null;
        if (hubRoutesUpdateRequestDto.destinationId() != null) {
            destination = hubServiceClient.getHubForHubRoutes(
                    hubRoutesUpdateRequestDto.destinationId());
        }
        log.info("HubServiceClientDto ={}", departure);
        log.info("HubServiceClientDto ={}", destination);
        // 허브서비스에 중앙허브 데이터 전부 조회
//        List<HubServiceResponseDto> centralHubList = hubServiceClient.getCentralHub();

        // 출발, 도착 좌표, 중앙허브 데이터 -> command로 바꿈
        HubRoutesCommand hubRoutesCommand = new HubRoutesCommand(
                departure,
                destination,
                null
        );

        HubRoutesEntity hubRoutesEntity = hubRoutesDomainService.updateHubRoutes(hubRoutesId,
                hubRoutesCommand);
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

    /**
     * oelivery 서버의 hubClient
     *
     * @param startHubId
     * @param endHubId
     * @return
     */
    @Override
    public HubRouteToDeliveryDto getHubRouteForOrder(UUID startHubId, UUID endHubId) {
        HubRoutesEntity hubRoutesEntity = hubRoutesDomainService.getHubRouteForOrder(startHubId,
                endHubId);
        return new HubRouteToDeliveryDto(
                String.valueOf(hubRoutesEntity.getDistance()),
                String.valueOf(hubRoutesEntity.getEstimatedTime())
        );
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
