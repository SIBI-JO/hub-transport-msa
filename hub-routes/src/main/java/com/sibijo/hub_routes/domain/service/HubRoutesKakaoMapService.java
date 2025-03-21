package com.sibijo.hub_routes.domain.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sibijo.common.exception.CustomException;
import com.sibijo.common.exception.codes.CommonExceptionCode;
import com.sibijo.hub_routes.application.dto.CentralHubDto;
import com.sibijo.hub_routes.application.dto.HubRoutesCommand;
import com.sibijo.hub_routes.application.dto.HubRoutesKakaoMapResponseDto;
import com.sibijo.hub_routes.application.dto.RouteCoordRequestDto;
import com.sibijo.hub_routes.application.dto.RouteTimeResponseDto;
import com.sibijo.hub_routes.infrastructure.dto.HubServiceClientDto;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubRoutesKakaoMapService {

    private final RestTemplate restTemplate;
    private static final String KAKAO_MAP_API_URL = "https://dapi.kakao.com/v2/local/search/address.json";
    private static final String KAKAO_MAP_ROUTE_API_URL = "https://apis-navi.kakaomobility.com/v1/waypoints/directions";
    @Value("${kakao.api.key}")
    private String apiKey;

    public HubRoutesKakaoMapResponseDto getCoordinats(String location) {
        URI uri = UriComponentsBuilder.fromUriString(KAKAO_MAP_API_URL)
                .queryParam("query", location)
                .build()
                .encode()
                .toUri();
        log.info("Kakao API URL: {}", uri);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAk " + apiKey);
        log.info("Kakao API Headers: {}", headers);

        RequestEntity<Void> request = RequestEntity
                .get(uri)
                .header("Authorization", "KakaoAK " + apiKey)
                .build();
        log.info("Kakao API Entity: {}", request);

        ResponseEntity<HubRoutesKakaoMapResponseDto> response = restTemplate.exchange(
                request,
                HubRoutesKakaoMapResponseDto.class);
        log.info("Kakao API befResponse: {}", response);
        log.info("Kakao API befbResponse: {}", response.getBody());
        HubRoutesKakaoMapResponseDto hubRoutesKakaoMapResponseDto = response.getBody();
        log.info("Kakao API Response: {}", hubRoutesKakaoMapResponseDto);

        return hubRoutesKakaoMapResponseDto;
    }

    /**
     * 허브 이동 경로 만들기 메소드 v1 : p2p 1. 허브 출발 좌표, 허브 도착 좌표 req 2. 허브 사이 길찾기 resp : 거리, 시간
     *
     * @param routeCoordRequestDto
     * @return
     */
    public RouteTimeResponseDto getDirections(RouteCoordRequestDto routeCoordRequestDto) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            String jsonBody = objectMapper.writeValueAsString(routeCoordRequestDto);
            URI uri = UriComponentsBuilder.fromUriString(KAKAO_MAP_ROUTE_API_URL)
                    .build()
                    .encode()
                    .toUri();

            log.info("Kakao Route API URL: {}", uri);

            RequestEntity<String> request = RequestEntity
                    .post(new URI(uri.toString()))
                    .header("Authorization", "KakaoAK " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(jsonBody);
            log.info("Kakao Route API Entity: {}", request);

            ResponseEntity<String> response = restTemplate.exchange(
                    request,
                    String.class
            );
            log.info("Kakao Route Response: {}", response.getBody());

            RouteTimeResponseDto routeTimeResponseDto = objectMapper.readValue(response.getBody(),
                    RouteTimeResponseDto.class);
            log.info("Kakao Route Response Body: {}",
                    objectMapper.writeValueAsString(routeTimeResponseDto));
            return routeTimeResponseDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(CommonExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    public CentralHubDto getCentralHub(HubRoutesCommand hubRoutesCommand) {
        BigDecimal departureToCentralMinDistance = new BigDecimal(Integer.MAX_VALUE);
        UUID departureAndCentralId = null;
        int departureAndCentralDuration = 0;

        BigDecimal destinationToCentralMinDistance = new BigDecimal(Integer.MAX_VALUE);
        UUID destinationAndCentralId = null;
        int destinationAndCentralDuration = 0;

        for (HubServiceClientDto central : hubRoutesCommand.centralHubList()) {
            // 도착에 가장 가까운 중앙허브
            RouteCoordRequestDto routeCoordRequestDto = buildRouteCoordRequest(
                    hubRoutesCommand.departure(), central);

            RouteTimeResponseDto departureAndCentral = getDirections(routeCoordRequestDto);
            compareAndUpdateMinDistance(departureAndCentral, departureToCentralMinDistance,
                    departureAndCentralId, central.getHubId(), departureAndCentralDuration);

            // 출발에 가장 가까운 중앙허브
            RouteCoordRequestDto destinationWithCentral = buildRouteCoordRequest(
                    hubRoutesCommand.destination(), central);
            RouteTimeResponseDto destinationAndCentral = getDirections(destinationWithCentral);
            compareAndUpdateMinDistance(destinationAndCentral, destinationToCentralMinDistance,
                    destinationAndCentralId, central.getHubId(), destinationAndCentralDuration);
        }

        if (departureAndCentralId != null && destinationAndCentralId != null) {
            if (departureAndCentralId.equals(destinationAndCentralId)) {
                // 출발지와 도착지가 같은 중앙 허브
                return new CentralHubDto(
                        departureAndCentralId,
                        null,
                        departureToCentralMinDistance,
                        destinationToCentralMinDistance,
                        departureAndCentralDuration,
                        destinationAndCentralDuration
                );
            }
        }

        return new CentralHubDto(
                departureAndCentralId,
                destinationAndCentralId,
                departureToCentralMinDistance,
                destinationToCentralMinDistance,
                departureAndCentralDuration,
                destinationAndCentralDuration
        );
    }

    // 출발지와 중앙허브 간의 거리 및 시간을 비교하여 업데이트
    private void compareAndUpdateMinDistance(RouteTimeResponseDto responseDto,
            BigDecimal minDistance, UUID hubId, UUID centralHubId, int duration) {
        if (responseDto.getRoutes().get(0).getSummary().getDistanceToKm()
                .compareTo(minDistance) < 0) {
            minDistance = responseDto.getRoutes().get(0).getSummary().getDistanceToKm();
            duration = responseDto.getRoutes().get(0).getSummary().getDurationToMinutes();
            hubId = centralHubId;
        }
    }

    // 출발지 및 도착지, 중앙허브 간의 요청을 빌드
    private RouteCoordRequestDto buildRouteCoordRequest(HubServiceClientDto location,
            HubServiceClientDto central) {
        return RouteCoordRequestDto.builder()
                .departure(RouteCoordRequestDto.Location.builder()
                        .x(String.valueOf(location.getLongitude()))
                        .y(String.valueOf(location.getLatitude()))
                        .angle(0)
                        .build())
                .destination(RouteCoordRequestDto.Location.builder()
                        .x(String.valueOf(central.getLongitude()))
                        .y(String.valueOf(central.getLatitude()))
                        .build())
                .wayPoints(List.of())
                .build();
    }

}
