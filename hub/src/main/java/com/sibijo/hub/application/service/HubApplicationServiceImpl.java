package com.sibijo.hub.application.service;

import com.sibijo.common.exception.CustomException;
import com.sibijo.hub.domain.model.HubType;
import com.sibijo.hub.presentation.dto.HubRequestDto;
import com.sibijo.hub.presentation.dto.HubResponseDto;
import com.sibijo.hub.presentation.dto.HubToRouteDto;
import com.sibijo.hub.presentation.dto.HubUpdateRequestDto;
import com.sibijo.hub.domain.model.HubEntity;
import com.sibijo.hub.domain.repository.HubRepository;
import com.sibijo.hub.domain.service.HubDomainService;
import com.sibijo.hub.exception.domain.HubDomainExceptionCode;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HubApplicationServiceImpl implements HubApplicationService {

    private final HubRepository hubRepository;
    private final HubDomainService hubDomainService;

    /**
     * 유저 서비스에서 회원 가입시 허브 서비스의 허브ID 존재 여부 확인
     * @param hubId
     * @return
     */
    @Override
    public boolean isHubExists(UUID hubId) {
        return hubRepository.existsByHubId(hubId);
    }

    /**허브 경로서비스 에서
     * @param hubId
     * @return
     */
    @Override
    public HubToRouteDto getHubForHubRoutes(UUID hubId) {
        HubEntity foundHub = findHubEntityOrElseThrow(hubId);
        return new HubToRouteDto(
                foundHub.getId(),
                foundHub.getHubName(),
                foundHub.getHubLocation(),
                foundHub.getLatitude(),
                foundHub.getLongitude(),
                foundHub.getHubType().getHubTypeName()
        );
    }

    /**
     * @param hubRequestDto
     * @return
     */
    @Override
    @Transactional
    public HubResponseDto createHub(HubRequestDto hubRequestDto) {

        // 유저 체크

        HubEntity hub = hubDomainService.createHubEntity(hubRequestDto);

        HubEntity hubEntity = hubRepository.save(hub);
        return convertHubResponseDto(hubEntity);
    }


    @Override
    public Page<HubResponseDto> searchHubs(
            String hubName,
            String hubLocation,
            String hubTypeName,
            Pageable pageable) {

        //허브 타입 체크
        HubType hubType = HubType.fromHubTypeName(hubTypeName);
        System.out.println("hubType: " + hubType);
        return hubRepository.searchHubs(hubName, hubLocation, hubType, pageable);
    }


    /**
     * @param hubId
     * @return
     */
    @Override
    public HubResponseDto getHub(UUID hubId) {
        return convertHubResponseDto(findHubEntityOrElseThrow(hubId));
    }


    /**
     * @param hubId
     * @param hubUpdateRequestDto
     * @return
     */
    @Override
    @Transactional
    public HubResponseDto updateHub(UUID hubId, HubUpdateRequestDto hubUpdateRequestDto) {
        HubEntity updatedHub = hubDomainService.updateHubEntity(hubId, hubUpdateRequestDto);
        return convertHubResponseDto(updatedHub);
    }

    /**
     * @param hubId
     */
    @Override
    @Transactional
    public void deleteHub(UUID hubId) {
        HubEntity hub = findHubEntityOrElseThrow(hubId);
        hubRepository.delete(hub);
    }


    private static HubResponseDto convertHubResponseDto(HubEntity hubEntity) {
        return new HubResponseDto(
                hubEntity.getId(),
                hubEntity.getHubName(),
                hubEntity.getHubLocation(),
                hubEntity.getLatitude(),
                hubEntity.getLongitude(),
                hubEntity.getHubType().getHubTypeName()
        );
    }

    private HubEntity findHubEntityOrElseThrow(UUID hubId) {
        return hubRepository.findById(hubId)
                .orElseThrow(() -> new CustomException(HubDomainExceptionCode.HUB_NOT_FOUND));
    }

}
