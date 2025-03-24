package com.sibijo.hub_routes.application.service;

import com.sibijo.common.exception.CustomException;
import com.sibijo.common.exception.codes.CommonExceptionCode;
import com.sibijo.common.utils.Auth.JwtUtil;
import com.sibijo.hub_routes.application.dto.HubRoutesCommand;
import com.sibijo.hub_routes.domain.model.HubRoutesEntity;
import com.sibijo.hub_routes.domain.repository.HubRoutesRepository;
import com.sibijo.hub_routes.domain.service.HubRoutesDomainService;
import com.sibijo.hub_routes.infrastructure.client.HubServiceClient;
import com.sibijo.hub_routes.infrastructure.dto.HubServiceClientDto;
import com.sibijo.hub_routes.presentation.dto.HubRouteToDeliveryDto;
import com.sibijo.hub_routes.presentation.dto.HubRoutesRequestDto;
import com.sibijo.hub_routes.presentation.dto.HubRoutesResponseDto;
import com.sibijo.hub_routes.presentation.dto.HubRoutesUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HubRoutesApplicationServiceImpl implements HubRoutesApplicationService {

    private final JwtUtil jwtUtil;
    private final HubRoutesRepository hubRoutesRepository;
    private final HubRoutesDomainService hubRoutesDomainService;
    private final HubServiceClient hubServiceClient;

    /**
     * @param hubRoutesRequestDto
     * @return
     */
    @Override
    @Transactional
    public HubRoutesResponseDto createHubRoutes(String token, HubRoutesRequestDto hubRoutesRequestDto) {
        checkUserAuth(token, "MASTER");
        log.info("hubRoutesRequestDto ={}", hubRoutesRequestDto);
        // 허브서비스에 허브 전체 조회
        HubServiceClientDto hubServiceClientDto = hubServiceClient.getHubForHubRoutes();

        // 출발, 도착, 허브 전체 데이터 -> command로 바꿈
        HubRoutesCommand hubRoutesCommand = new HubRoutesCommand(
                hubRoutesRequestDto.departureId(),
                hubRoutesRequestDto.destinationId(),
                hubServiceClientDto.getHubs()
        );
        log.info("createHubRoutesCommand ={}", hubRoutesCommand);

        HubRoutesEntity hubRoutesEntity = hubRoutesDomainService.createHubRoutes(
                hubRoutesCommand);

        String hashedSequenceJson = hubRoutesEntity.getHashSequence();

        //기존 데이터 있으면 save 안함
        Optional<HubRoutesEntity> compareHubRoutesEntity = hubRoutesRepository.findByHashSequence(hashedSequenceJson);

        if (compareHubRoutesEntity.isEmpty()) {
            HubRoutesEntity savedHubRoutesEntity = hubRoutesRepository.save(hubRoutesEntity);
            return convertToHubRoutesResponseDto(savedHubRoutesEntity);
        }
        return convertToHubRoutesResponseDto(compareHubRoutesEntity.get());
    }

    /**
     * @param validatedPageable
     * @return
     */
    @Override
    public Page<HubRoutesResponseDto> searchHubRoutes(String token, Pageable validatedPageable) {
        checkUserAuth(token, null);
        return hubRoutesRepository.searchHubRoutes(validatedPageable);
    }

    /**
     * @param hubRoutesId
     * @return
     */
    @Override
    public HubRoutesResponseDto getHubRoute(String token, UUID hubRoutesId) {
        checkUserAuth(token, null);
        return convertToHubRoutesResponseDto(hubRoutesDomainService.getHubRoute(hubRoutesId));
    }

    /**
     * 단순 업데이트
     *
     * @param hubRoutesId
     * @param hubRoutesUpdateRequestDto
     * @return
     */
    @Override
    @Transactional
    public HubRoutesResponseDto updateHubRoutes(String token, UUID hubRoutesId,
                                                HubRoutesUpdateRequestDto hubRoutesUpdateRequestDto) {
        checkUserAuth(token, "MASTER");
        HubRoutesEntity hubRoutesEntity = hubRoutesDomainService.updateHubRoutes(hubRoutesId,
                hubRoutesUpdateRequestDto);
        return convertToHubRoutesResponseDto(hubRoutesEntity);
    }

    /**
     * @param hubRoutesId
     */
    @Override
    @Transactional
    public void deleteHubRoute(String token, UUID hubRoutesId) {
        checkUserAuth(token, "MASTER");
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
                String.valueOf(hubRoutesEntity.getEstimatedTime()),
                hubRoutesEntity.getSequence()
        );
    }

    private static HubRoutesResponseDto convertToHubRoutesResponseDto(
            HubRoutesEntity hubRoutesEntity) {
        return new HubRoutesResponseDto(
                hubRoutesEntity.getId(),
                hubRoutesEntity.getDepartureId(),
                hubRoutesEntity.getDestinationId(),
                hubRoutesEntity.getDistance(),
                hubRoutesEntity.getEstimatedTime(),
                hubRoutesEntity.getSequence(),
                hubRoutesEntity.getHashSequence()
        );
    }

    private void checkUserAuth(String token, String requiredRole) {
        // 유저 체크
        String role = jwtUtil.extractRole(token);
        Long userId = jwtUtil.extractUserID(token);
        if (role == null || userId == null) {
            throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
        }
        // 역할 체크
        if (requiredRole != null && !requiredRole.equals(role)) {
            throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
        }
    }

}
