package com.sibijo.hub_routes.domain.service;

import com.sibijo.common.exception.CustomException;
import com.sibijo.hub_routes.application.dto.HubRoutesCommand;
import com.sibijo.hub_routes.application.dto.RouteCoordRequestDto;
import com.sibijo.hub_routes.application.dto.RouteTimeResponseDto;
import com.sibijo.hub_routes.domain.exception.HubRoutesDomainExceptionCode;
import com.sibijo.hub_routes.domain.model.HubRoutesEntity;
import com.sibijo.hub_routes.domain.repository.HubRoutesRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class HubRoutesDomainServiceImpl implements HubRoutesDomainService {

    private final HubRoutesRepository hubRoutesRepository;
    private final HubRoutesKakaoMapService hubRoutesKakaoMapService;

    /**
     * @param hubRoutesCommand
     * @return
     */
    @Override
    public HubRoutesEntity createHubRoutes(HubRoutesCommand hubRoutesCommand) {

        /**
         * 경로 만들기
         * 출발 -> 중앙 -> 도착 : 무조건 중앙 거쳐야됨
         * 1. 주문의 출발 ID -> 허브의 출발 ID의 주소
         * 2. 출발 주소 와 가장 가까운 중앙허브 찾기
         * 3. 중앙허브와 도착 허브와 배송도착지 가장 가까운 경로 찾기
         * 4.
         */
        log.info("createHubRoutesCommand={}", hubRoutesCommand);

        //출발과 가까운 중앙허브 찾기
//        CentralHubDto centralHubDto = hubRoutesKakaoMapService.getCentralHub(
//                createHubRoutesCommand);


        //출발 , 도착, 경유 좌표 넘기기 -> p2p
        UUID centralId = null;
        RouteCoordRequestDto routeCoordRequestDto = RouteCoordRequestDto.builder()
                .departure(RouteCoordRequestDto.Location.builder()
                        .x(String.valueOf(hubRoutesCommand.departure().getLongitude()))
                        .y(String.valueOf(hubRoutesCommand.departure().getLatitude()))
                        .angle(0)
                        .build())
                .destination(RouteCoordRequestDto.Location.builder()
                        .x(String.valueOf(hubRoutesCommand.destination().getLongitude()))
                        .y(String.valueOf(hubRoutesCommand.destination().getLatitude()))
                        .build())
                .wayPoints(List.of())
                .build();

        RouteTimeResponseDto routeTimeResponseDto = hubRoutesKakaoMapService.getDirections(
                routeCoordRequestDto);
        RouteTimeResponseDto.RouteSummary summary = routeTimeResponseDto.getRoutes().get(0)
                .getSummary();
        BigDecimal distance = summary.getDistanceToKm();
        Integer duration = Integer.valueOf(summary.getDurationToMinutes());

        return HubRoutesEntity.builder()
                .departureId(hubRoutesCommand.departure().getHubId())
                .destinationId(hubRoutesCommand.destination().getHubId())
                .centralId(centralId) //null
                .distance(distance)
                .estimatedTime(duration)
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
     * update 고민 -> 안해도 될 것 같음
     * @param hubRoutesId
     * @param hubRoutesCommand
     * @return
     */
    @Override
    public HubRoutesEntity updateHubRoutes(UUID hubRoutesId,
            HubRoutesCommand hubRoutesCommand) {
        HubRoutesEntity originalHubRoutesEntity = findHubRoutesById(hubRoutesId);
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

    /**
     * delivery 서버에게
     *
     * @param startHubId
     * @param endHubId
     * @return
     */
    @Override
    public HubRoutesEntity getHubRouteForOrder(UUID startHubId, UUID endHubId) {
        return hubRoutesRepository.findByDepartureIdAndDestinationId(startHubId, endHubId)
                .orElseThrow(() -> new CustomException(
                        HubRoutesDomainExceptionCode.HUB_ROUTES_NOT_FOUND));
    }

    private HubRoutesEntity findHubRoutesById(UUID hubRoutesId) {
        return hubRoutesRepository.findById(hubRoutesId)
                .orElseThrow(() -> new CustomException(
                        HubRoutesDomainExceptionCode.HUB_ROUTES_NOT_FOUND));

    }
}
