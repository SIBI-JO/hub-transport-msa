package com.sibijo.hub_routes.domain.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sibijo.common.exception.CustomException;
import com.sibijo.common.exception.codes.CommonExceptionCode;
import com.sibijo.hub_routes.application.dto.RouteCoordRequestDto;
import com.sibijo.hub_routes.application.dto.RouteTimeResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubRoutesKakaoMapService {

    private final RestTemplate restTemplate;
    private static final String KAKAO_MAP_ROUTE_API_URL = "https://apis-navi.kakaomobility.com/v1/waypoints/directions";
    @Value("${kakao.api.key}")
    private String apiKey;


    /**
     * 허브 이동 경로 만들기 메소드 v1 : hub to hub relay
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

}
