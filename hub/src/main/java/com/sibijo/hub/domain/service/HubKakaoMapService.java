package com.sibijo.hub.domain.service;

import com.sibijo.hub.application.dto.HubKakaoMapResponseDto;
import java.net.URI;
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
public class HubKakaoMapService {

    private final RestTemplate restTemplate;
    private static final String KAKAO_MAP_API_URL = "https://dapi.kakao.com/v2/local/search/address.json";

    @Value("${kakao.api.key}")
    private String apiKey;

    public HubKakaoMapResponseDto getCoordinats(String location) {
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

        ResponseEntity<HubKakaoMapResponseDto> response = restTemplate.exchange(
                request,
                HubKakaoMapResponseDto.class);
        log.info("Kakao API befResponse: {}", response);
        log.info("Kakao API befbResponse: {}", response.getBody());
        HubKakaoMapResponseDto hubKakaoMapResponseDto = response.getBody();
        log.info("Kakao API Response: {}", hubKakaoMapResponseDto.getDocuments());

        return hubKakaoMapResponseDto;
    }
}
