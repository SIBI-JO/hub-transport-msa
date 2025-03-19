package com.sibijo.hub_routes.domain.service;

import com.sibijo.common.exception.CustomException;
import com.sibijo.hub_routes.domain.exception.HubRoutesDomainExceptionCode;
import com.sibijo.hub_routes.domain.model.HubRoutesEntity;
import com.sibijo.hub_routes.domain.repository.HubRoutesRepository;
import com.sibijo.hub_routes.presentation.dto.HubRoutesRequestDto;
import com.sibijo.hub_routes.presentation.dto.HubRoutesUpdateRequestDto;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class HubRoutesDomainServiceImpl implements HubRoutesDomainService {

    private final HubRoutesRepository hubRoutesRepository;

    /**
     * @param hubRoutesRequestDto
     * @return
     */
    @Override
    public HubRoutesEntity createHubRoutes(HubRoutesRequestDto hubRoutesRequestDto) {
        //중복 체크
        if (hubRoutesRepository.existsByDepartureAndDestinationID(hubRoutesRequestDto.departureId(),
                hubRoutesRequestDto.destinationId())) {
            throw new CustomException(HubRoutesDomainExceptionCode.HUB_ROUTES_IS_DUPLICATED);
        }
        /**
         * 경로 만들기
         * 출발 -> 중앙 -> 도착 : 무조건 중앙 거쳐야됨
         *
         */
        UUID centralId = UUID.randomUUID();
        BigDecimal distance = new BigDecimal("100");
        Integer estimatedTime = 5;

        return HubRoutesEntity.builder()
                .departureId(hubRoutesRequestDto.departureId())
                .destinationId(hubRoutesRequestDto.destinationId())
                .centralId(centralId)
                .distance(distance)
                .estimatedTime(estimatedTime)
                .build();
    }

    /**
     * @param hubRoutesId
     * @return
     */
    @Override
    public HubRoutesEntity getHubRoute(UUID hubRoutesId) {
        return findHubRoutesById(hubRoutesId);
    }

    /**
     * @param hubRoutesId
     * @param hubRoutesUpdateRequestDto
     * @return
     */
    @Override
    public HubRoutesEntity updateHubRoutes(UUID hubRoutesId,
            HubRoutesUpdateRequestDto hubRoutesUpdateRequestDto) {
        HubRoutesEntity originalHubRoutesEntity = findHubRoutesById(hubRoutesId);

        // 오리지널이랑 똑같은 입력 -> 에러처리 -> 컨트롤러 or 애플리케이션단에서

        if (hubRoutesUpdateRequestDto.departureId() != null) {
            originalHubRoutesEntity.updateDepartureId(hubRoutesUpdateRequestDto.departureId());
            /**
             * 경로 생성하기
             * 기존 도착지, 중앙, 거리, 시간
             */
            UUID centralId = UUID.randomUUID();
            BigDecimal distance = new BigDecimal("100");
            Integer estimatedTime = 5;
            originalHubRoutesEntity.updateRoutes(centralId, distance, estimatedTime);
        }
        if (hubRoutesUpdateRequestDto.destinationId() != null) {
            originalHubRoutesEntity.updateDestinationId(hubRoutesUpdateRequestDto.destinationId());
            /**
             * 경로 생성하기
             * 기존 출발지, 중앙, 거리, 시간
             */
            UUID centralId = UUID.randomUUID();
            BigDecimal distance = new BigDecimal("100");
            Integer estimatedTime = 5;
            originalHubRoutesEntity.updateRoutes(centralId, distance, estimatedTime);
        }

        return hubRoutesRepository.save(originalHubRoutesEntity);
    }

    /**
     * @param hubRoutesId
     * @return
     */
    @Override
    public HubRoutesEntity deleteHubRoute(UUID hubRoutesId) {
        return findHubRoutesById(hubRoutesId);
    }

    private HubRoutesEntity findHubRoutesById(UUID hubRoutesId) {
        return hubRoutesRepository.findById(hubRoutesId)
                .orElseThrow(() -> new CustomException(
                        HubRoutesDomainExceptionCode.HUB_ROUTES_NOT_FOUND));

    }
}
