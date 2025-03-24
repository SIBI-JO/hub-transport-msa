package com.sibijo.hub_routes.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sibijo.common.exception.CustomException;
import com.sibijo.common.exception.codes.CommonExceptionCode;
import com.sibijo.hub_routes.application.dto.HubRoutesCommand;
import com.sibijo.hub_routes.domain.exception.HubRoutesDomainExceptionCode;
import com.sibijo.hub_routes.domain.model.HubRoutesEntity;
import com.sibijo.hub_routes.domain.repository.HubRoutesRepository;
import com.sibijo.hub_routes.infrastructure.dto.BestRouteResponseDto;
import com.sibijo.hub_routes.presentation.dto.HubRoutesUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class HubRoutesDomainServiceImpl implements HubRoutesDomainService {

    private final HubRoutesRepository hubRoutesRepository;
    private final DijkstraService dijkstraService;

    /**
     * @param hubRoutesCommand
     * @return
     */
    @Override
    public HubRoutesEntity createHubRoutes(HubRoutesCommand hubRoutesCommand) {

        /**
         * 경로 만들기
         *
         */
        log.info("createHubRoutesCommand={}", hubRoutesCommand);

        //출발, 도착, 전체 허브 리스트 -> hub to hub relay
        //다익스트라 호출
        BestRouteResponseDto bestRouteResponseDto = dijkstraService.findShortestPath(hubRoutesCommand);

        Map<Integer, List<String>> bestPathMap = bestRouteResponseDto.getBestPathMap();
        Map<Integer, String> sequenceMap = new HashMap<>();
        for (Map.Entry<Integer, List<String>> entry : bestPathMap.entrySet()) {
            List<String> hubs = entry.getValue();
            for (int i = 0; i < hubs.size(); i++) {
                sequenceMap.put(i, hubs.get(i));
            }
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String sequenceJson = objectMapper.writeValueAsString(sequenceMap);

            return HubRoutesEntity.builder()
                    .departureId(bestRouteResponseDto.getDepartureHubId())
                    .destinationId(bestRouteResponseDto.getDestinationHubId())
                    .distance(bestRouteResponseDto.getBestRouteDistance())
                    .estimatedTime(bestRouteResponseDto.getBestRouteTime())
                    .sequence(sequenceJson)
                    .hashSequence(generateSha256Hash(sequenceJson))
                    .build();
        } catch (JsonProcessingException e) {
            throw new CustomException(CommonExceptionCode.INTERNAL_SERVER_ERROR);
        }
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
     * 단순 update
     *
     * @param hubRoutesId
     * @param hubRoutesUpdateRequestDto
     * @return
     */
    @Override
    public HubRoutesEntity updateHubRoutes(UUID hubRoutesId,
                                           HubRoutesUpdateRequestDto hubRoutesUpdateRequestDto) {
        HubRoutesEntity originalHubRoutesEntity = findHubRoutesById(hubRoutesId);

        if (hubRoutesUpdateRequestDto.departureId() != null) {
            originalHubRoutesEntity.updateDepartureId(hubRoutesUpdateRequestDto.departureId());
        }
        if (hubRoutesUpdateRequestDto.destinationId() != null) {
            originalHubRoutesEntity.updateDestinationId(hubRoutesUpdateRequestDto.destinationId());
        }
        if (hubRoutesUpdateRequestDto.distance() != null) {
            originalHubRoutesEntity.updateDistance(
                    new BigDecimal(hubRoutesUpdateRequestDto.distance()));
        }
        if (hubRoutesUpdateRequestDto.estimatedTime() != null) {
            originalHubRoutesEntity.updateEstimatedTime(
                    Integer.valueOf(hubRoutesUpdateRequestDto.estimatedTime()));
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

    private String generateSha256Hash(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(data.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new CustomException(CommonExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }
}
