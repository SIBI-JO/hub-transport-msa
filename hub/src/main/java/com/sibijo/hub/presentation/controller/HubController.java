package com.sibijo.hub.presentation.controller;

import com.sibijo.common.dto.ApiResponse;
import com.sibijo.common.utils.Auth.JwtUtil;
import com.sibijo.common.utils.page.PageableUtils;
import com.sibijo.hub.application.service.HubApplicationService;
import com.sibijo.hub.domain.repository.HubRepository;
import com.sibijo.hub.presentation.dto.HubRequestDto;
import com.sibijo.hub.presentation.dto.HubResponseDto;
import com.sibijo.hub.presentation.dto.HubToRouteDto;
import com.sibijo.hub.presentation.dto.HubUpdateRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/hubs")
@RequiredArgsConstructor
public class HubController {

    private final HubApplicationService hubApplicationService;
    private final JwtUtil jwtUtil;
    private final HubRepository hubRepository;

    //회원가입 시 허브 아이디 존재 확인
    @GetMapping("/{hubId}/exists")
    public ResponseEntity<ApiResponse<Boolean>> hubExists(@PathVariable("hubId") UUID hubId) {
        boolean exists = hubApplicationService.isHubExists(hubId);
        return ResponseEntity.ok(ApiResponse.success("허브 존재 확인 성공", exists));
    }

    @GetMapping("/hub-routes")
    public HubToRouteDto getHubForHubRoutes() {
        return hubApplicationService.getHubForHubRoutes();
    }

    /**
     * @param hubRequestDto
     * @param request
     * @return auth : master
     */
    @PostMapping
    public ResponseEntity<ApiResponse<HubResponseDto>> createHub(
            @Valid @RequestBody HubRequestDto hubRequestDto,
            HttpServletRequest request
    ) {
        String token = jwtUtil.extractToken(request);

        HubResponseDto hubResponseDto = hubApplicationService.createHub(
                token,
                hubRequestDto
        );

        return ResponseEntity.ok(ApiResponse.success("허브 생성 성공", hubResponseDto));

    }

    /**
     * @param hubName
     * @param hubLocation
     * @param hubTypeName
     * @param pageable
     * @return auth : all
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<HubResponseDto>>> searchHubs(
            @RequestParam(
                    name = "hubName",
                    defaultValue = "",
                    required = false
            ) String hubName,
            @RequestParam(
                    name = "hubLocation",
                    defaultValue = "",
                    required = false
            ) String hubLocation,
            @RequestParam(
                    name = "hubTypeName",
                    required = false
            ) String hubTypeName,
            @PageableDefault(
                    size = 10,
                    page = 1,
                    sort = {"createdAt", "updatedAt"},
                    direction = Direction.ASC
            ) Pageable pageable,
            HttpServletRequest request
    ) {
        String token = jwtUtil.extractToken(request);
        Pageable validatedPageable = PageableUtils.validatePageable(pageable);
        Page<HubResponseDto> searchHubs = hubApplicationService.searchHubs(
                token,
                hubName,
                hubLocation,
                hubTypeName,
                validatedPageable
        );

        return ResponseEntity.ok(ApiResponse.success("허브 검색 성공", searchHubs));
    }

    /**
     * @param hubId
     * @param request
     * @return auth : all
     */
    @GetMapping("/{hubId}")
    public ResponseEntity<ApiResponse<HubResponseDto>> getHub(
            @PathVariable("hubId") UUID hubId,
            HttpServletRequest request) {
        String token = jwtUtil.extractToken(request);
        HubResponseDto hubResponseDto = hubApplicationService.getHub(token, hubId);
        return ResponseEntity.ok(ApiResponse.success("허브 단일 조회 성공", hubResponseDto));
    }


    /**
     * @param hubId
     * @param hubUpdateRequestDto
     * @return auth : master
     */
    @PatchMapping("/{hubId}")
    public ResponseEntity<ApiResponse<HubResponseDto>> updateHub(
            @PathVariable UUID hubId,
            @RequestBody HubUpdateRequestDto hubUpdateRequestDto,
            HttpServletRequest request) {
        String token = jwtUtil.extractToken(request);
        HubResponseDto hubResponseDto = hubApplicationService.updateHub(token, hubId, hubUpdateRequestDto);
        return ResponseEntity.ok(ApiResponse.success("허브 수정 성공", hubResponseDto));
    }

    /**
     * @param hubId
     * @return auth : master
     */
    @DeleteMapping("/{hubId}")
    public ResponseEntity<ApiResponse<?>> deleteHub(@PathVariable("hubId") UUID hubId, HttpServletRequest request) {
        String token = jwtUtil.extractToken(request);
        hubApplicationService.deleteHub(token, hubId);
        return ResponseEntity.ok(ApiResponse.success("허브 삭제 성공"));
    }
}
