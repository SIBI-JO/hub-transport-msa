package com.sibijo.hub.domain.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sibijo.common.exception.CustomException;
import com.sibijo.hub.domain.model.HubEntity;
import com.sibijo.hub.domain.model.HubType;
import com.sibijo.hub.domain.repository.HubRepository;
import com.sibijo.hub.exception.domain.HubDomainExceptionCode;
import com.sibijo.hub.presentation.dto.HubRequestDto;
import com.sibijo.hub.presentation.dto.HubUpdateRequestDto;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class HubDomainServiceImpl implements HubDomainService {

    private final HubRepository hubRepository;

    private final RestTemplate restTemplate;

    private final WebClient webClient;

    @Value("${google.api.key}")
    private String apiKey;

    private static final String GOOGLE_MAPS_API_URL = "https://maps.googleapis.com/maps/api/geocode/json";

    /**
     * @param hubRequestDto
     * @return
     */
    @Override
    public HubEntity createHubEntity(HubRequestDto hubRequestDto) {

        //허브 타입 체크
        HubType hubType = HubType.fromHubTypeName(hubRequestDto.hubTypeName());

        //허브 중복 체크 -> 좌표로 중복체크
        if (hubRepository.existsByHubNameAndHubLocation(hubRequestDto.hubName(),
                hubRequestDto.hubLocation())) {
            throw new CustomException(HubDomainExceptionCode.HUB_IS_DUPLICATED);
        }

        //허브 좌표 생성 -> domainService
        BigDecimal latitude = new BigDecimal("37.5665");  // 서울 위도 예시
        BigDecimal longitude = new BigDecimal("126.9780"); // 서울 경도 예시

//        BigDecimal[] corrdinates = getCoordinatesGooglePlaces(hubRequestDto.hubLocation());
//        BigDecimal latitude = corrdinates[0];
//        BigDecimal longitude = corrdinates[1];

        return HubEntity.builder()
                .hubName(hubRequestDto.hubName())
                .hubLocation(hubRequestDto.hubLocation())
                .latitude(latitude)
                .longitude(longitude)
                .hubType(hubType)
                .build();
    }

    /**
     * @param hubId
     * @param hubUpdateRequestDto
     * @return
     */
    @Override
    public HubEntity updateHubEntity(UUID hubId, HubUpdateRequestDto hubUpdateRequestDto) {
        HubEntity originHub = hubRepository.findById(hubId)
                .orElseThrow(() -> new CustomException(HubDomainExceptionCode.HUB_NOT_FOUND));

        if (hubUpdateRequestDto.hubName() != null) {
            originHub.updateHubName(hubUpdateRequestDto.hubName());
        }
        if (hubUpdateRequestDto.hubLocation() != null) {
            originHub.updateHubLocation(hubUpdateRequestDto.hubLocation());
        }
        if (hubUpdateRequestDto.hubTypeName() != null) {
            //허브타입 검증
            HubType hubType = HubType.fromHubTypeName(hubUpdateRequestDto.hubTypeName());

            originHub.updateHubType(hubType);
        }

        return hubRepository.save(originHub);
    }

    private BigDecimal[] getCoordinatesGooglePlaces(String location) {
        String encodedAddress = URLEncoder.encode(location, StandardCharsets.UTF_8);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/maps/api/geocode/json")
                        .queryParam("address", encodedAddress)
                        .queryParam("key", apiKey)
                        .queryParam("language", "ko")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    log.error("Client error: {}", response.statusCode());
                    return Mono.error(new CustomException(HubDomainExceptionCode.INVALID_REQUEST));
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    log.error("Server error: {}", response.statusCode());
                    return Mono.error(new CustomException(HubDomainExceptionCode.API_SERVER_ERROR));
                })
                .bodyToMono(String.class)
                .map(this::parseResponseToJson)
                .block(Duration.ofSeconds(5));

    }

    //좌표 가져오기 구글 맵 api
    private BigDecimal[] getCoordinatesGooglePlacesRest(String location) {
        location = location.replace(" ", "+").trim();
        String url = UriComponentsBuilder.fromHttpUrl(GOOGLE_MAPS_API_URL)
                .queryParam("address", location)
                .queryParam("key", apiKey)
                .queryParam("language", "ko")
                .encode(StandardCharsets.UTF_8)
                .build().toUriString();

        log.info("before address : " + location);
        log.info("before url : " + url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        String response = restTemplate.getForObject(url, String.class);

        log.info("current address : " + location);
        log.info("current url : " + url);
        // log.info("API response : " + response);

        // RestTemplate을 사용하여 API 요청 및 응답
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET,
                new HttpEntity<>(headers), String.class);

        // 응답 상태 코드 확인
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String response = responseEntity.getBody();
            log.info("API response: {}", response);

            // 응답에서 좌표를 추출
            return parseResponseToJson(response);
        } else {
            log.error("Google Maps API 요청 실패: {}", responseEntity.getStatusCode());
            throw new CustomException(HubDomainExceptionCode.API_RESPONSE_ERROR);
        }
    }

    private BigDecimal[] parseResponseToJson(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);
            log.info("JSON root : {}", root);
            log.info("response : {}", response);
            // 1. API 상태 코드 검증
            String status = root.path("status").asText();
            if (!"OK".equalsIgnoreCase(status)) {
                log.error("Google API 응답 오류: {}", status);
                throw new CustomException(HubDomainExceptionCode.API_RESPONSE_ERROR);
            }

            // 2. 결과 존재 여부 확인
            JsonNode results = root.path("results");
            if (results.isEmpty()) {
                log.error("검색 결과 없음");
                throw new CustomException(HubDomainExceptionCode.LOCATION_NOT_FOUND);
            }

            // 3. 좌표 추출
            JsonNode firstResult = results.get(0);
            JsonNode location = firstResult.path("geometry").path("location");

            BigDecimal latitude = new BigDecimal(location.path("lat").asText());
            BigDecimal longitude = new BigDecimal(location.path("lng").asText());

            log.info("추출 좌표 - 위도: {}, 경도: {}", latitude, longitude);
            return new BigDecimal[]{latitude, longitude};

        } catch (NumberFormatException e) {
            log.error("좌표 변환 오류: {}", e.getMessage());
            throw new CustomException(HubDomainExceptionCode.INVALID_COORDINATE_FORMAT);
        } catch (Exception e) {
            log.error("JSON 파싱 오류: {}", e.getMessage());
            throw new CustomException(HubDomainExceptionCode.INVALID_API_RESPONSE);
        }
    }

}

