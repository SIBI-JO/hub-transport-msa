package com.sibijo.hub.domain.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sibijo.common.exception.CustomException;
import com.sibijo.hub.application.dto.HubKakaoMapResponseDto;
import com.sibijo.hub.domain.model.HubEntity;
import com.sibijo.hub.domain.model.HubType;
import com.sibijo.hub.domain.repository.HubRepository;
import com.sibijo.hub.exception.domain.HubDomainExceptionCode;
import com.sibijo.hub.presentation.dto.HubRequestDto;
import com.sibijo.hub.presentation.dto.HubUpdateRequestDto;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class HubDomainServiceImpl implements HubDomainService {

    private final HubRepository hubRepository;

    private final HubKakaoMapService hubKakaoMapService;

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

        HubKakaoMapResponseDto hubKakaoMapResponseDto = hubKakaoMapService.getCoordinats(
                hubRequestDto.hubLocation());
        log.info("Kakao API Headers: {}", hubKakaoMapResponseDto.getDocuments());

        String lat = hubKakaoMapResponseDto.getDocuments().get(0).getY();
        String lon = hubKakaoMapResponseDto.getDocuments().get(0).getX();
        BigDecimal latitude = new BigDecimal(lat);
        BigDecimal longitude = new BigDecimal(lon);

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

