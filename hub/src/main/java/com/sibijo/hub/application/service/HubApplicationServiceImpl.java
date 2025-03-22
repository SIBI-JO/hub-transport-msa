package com.sibijo.hub.application.service;

import com.sibijo.common.exception.CustomException;
import com.sibijo.common.exception.codes.CommonExceptionCode;
import com.sibijo.common.utils.Auth.JwtUtil;
import com.sibijo.hub.domain.model.HubEntity;
import com.sibijo.hub.domain.model.HubType;
import com.sibijo.hub.domain.repository.HubRepository;
import com.sibijo.hub.domain.service.HubDomainService;
import com.sibijo.hub.exception.domain.HubDomainExceptionCode;
import com.sibijo.hub.presentation.dto.HubRequestDto;
import com.sibijo.hub.presentation.dto.HubResponseDto;
import com.sibijo.hub.presentation.dto.HubToRouteDto;
import com.sibijo.hub.presentation.dto.HubUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HubApplicationServiceImpl implements HubApplicationService {

    private final HubRepository hubRepository;
    private final HubDomainService hubDomainService;
    private final JwtUtil jwtUtil;

    /**
     * 유저 서비스에서 회원 가입시 허브 서비스의 허브ID 존재 여부 확인
     *
     * @param hubId
     * @return
     */
    @Override
    public boolean isHubExists(UUID hubId) {
        return hubRepository.existsByHubId(hubId);
    }

    /**
     * 허브 경로서비스 에서
     *
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
    public HubResponseDto createHub(String token, HubRequestDto hubRequestDto) {

        // 유저 체크
        checkUserAuth(token);

        HubEntity hub = hubDomainService.createHubEntity(hubRequestDto);

        HubEntity hubEntity = hubRepository.save(hub);
        return convertHubResponseDto(hubEntity);
    }


    @Override
    public Page<HubResponseDto> searchHubs(
            String token,
            String hubName,
            String hubLocation,
            String hubTypeName,
            Pageable pageable) {

        checkUserAuth(token);
        //허브 타입 체크
        HubType hubType = HubType.fromHubTypeName(hubTypeName);
        System.out.println("hubType: " + hubType);
        return hubRepository.searchHubs(hubName, hubLocation, hubType, pageable);
    }


    /**
     * @param token
     * @param hubId
     * @return
     */
    @Override
    public HubResponseDto getHub(String token, UUID hubId) {
        checkUserAuth(token);
        return convertHubResponseDto(findHubEntityOrElseThrow(hubId));
    }


    /**
     * @param hubId
     * @param hubUpdateRequestDto
     * @return
     */
    @Override
    @Transactional
    public HubResponseDto updateHub(String token, UUID hubId, HubUpdateRequestDto hubUpdateRequestDto) {
        checkUserAuth(token);
        HubEntity updatedHub = hubDomainService.updateHubEntity(hubId, hubUpdateRequestDto);
        return convertHubResponseDto(updatedHub);
    }

    /**
     * @param hubId
     */
    @Override
    @Transactional
    public void deleteHub(String token, UUID hubId) {
        checkUserAuth(token);
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

    private void checkUserAuth(String token) {
        // 유저 체크
        String role = jwtUtil.extractRole(token);
        Long userId = jwtUtil.extractUserID(token);
        if (role == null || userId == null) {
            throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
        }
    }

}
