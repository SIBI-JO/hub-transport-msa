package com.sibijo.hub_routes.presentation.controller;

import com.sibijo.common.dto.ApiResponse;
import com.sibijo.common.utils.Auth.JwtUtil;
import com.sibijo.common.utils.page.PageableUtils;
import com.sibijo.hub_routes.application.service.HubRoutesApplicationService;
import com.sibijo.hub_routes.presentation.dto.HubRouteToDeliveryDto;
import com.sibijo.hub_routes.presentation.dto.HubRoutesRequestDto;
import com.sibijo.hub_routes.presentation.dto.HubRoutesResponseDto;
import com.sibijo.hub_routes.presentation.dto.HubRoutesUpdateRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/hub-routes")
@RequiredArgsConstructor
public class HubRoutesController {

    private final HubRoutesApplicationService hubRoutesApplicationService;
    private final JwtUtil jwtUtil;


    @GetMapping("/order")
    public HubRouteToDeliveryDto getHubRouteForOrder(
            @RequestParam UUID startHubId,
            @RequestParam UUID endHubId) {
        HubRouteToDeliveryDto hubRouteToDeliveryDto = hubRoutesApplicationService.getHubRouteForOrder(
                startHubId, endHubId);
        return hubRouteToDeliveryDto;
    }

    /**
     * @param hubRoutesRequestDto
     * @return auth : master
     */
    @PostMapping
    public ResponseEntity<ApiResponse<HubRoutesResponseDto>> createHubRoutes(
            @Valid @RequestBody HubRoutesRequestDto hubRoutesRequestDto,
            HttpServletRequest request
    ) {
        String token = jwtUtil.extractToken(request);
        HubRoutesResponseDto hubRoutesResponseDto = hubRoutesApplicationService.createHubRoutes(
                token,
                hubRoutesRequestDto
        );

        return ResponseEntity.ok(ApiResponse.success("허브 경로 생성 성공", hubRoutesResponseDto));
    }

    /**
     * @param pageable
     * @return auth : all
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<HubRoutesResponseDto>>> searchHubRoutes(
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
        Page<HubRoutesResponseDto> searchedHubRoutes = hubRoutesApplicationService.searchHubRoutes(
                token,
                validatedPageable
        );

        return ResponseEntity.ok(ApiResponse.success("허브 경로 검색 성공", searchedHubRoutes));
    }

    @GetMapping("/{hubRoutesId}")
    public ResponseEntity<ApiResponse<HubRoutesResponseDto>> getHubRoute(
            @PathVariable("hubRoutesId") UUID hubRoutesId,
            HttpServletRequest request) {
        String token = jwtUtil.extractToken(request);
        HubRoutesResponseDto hubRoutesResponseDto = hubRoutesApplicationService.getHubRoute(
                token,
                hubRoutesId);

        return ResponseEntity.ok(ApiResponse.success("허브 경로 단일 조회 성공", hubRoutesResponseDto));
    }

    /**
     * update시
     *
     * @param hubRoutesId
     * @param hubRoutesUpdateRequestDto
     * @return auth : master
     */
    @PatchMapping("/{hubRoutesId}")
    public ResponseEntity<ApiResponse<HubRoutesResponseDto>> updateHubRoute(
            @PathVariable("hubRoutesId") UUID hubRoutesId,
            @RequestBody HubRoutesUpdateRequestDto hubRoutesUpdateRequestDto,
            HttpServletRequest request
    ) {
        String token = jwtUtil.extractToken(request);
        HubRoutesResponseDto hubRoutesResponseDto = hubRoutesApplicationService.updateHubRoutes(
                token, hubRoutesId, hubRoutesUpdateRequestDto);

        return ResponseEntity.ok(ApiResponse.success("허브 경로 수정 성공", hubRoutesResponseDto));
    }

    @DeleteMapping("/{hubRoutesId}")
    public ResponseEntity<ApiResponse<HubRoutesResponseDto>> deleteHubRoute(
            @PathVariable("hubRoutesId") UUID hubRoutesId,
            HttpServletRequest request
    ) {
        String token = jwtUtil.extractToken(request);
        hubRoutesApplicationService.deleteHubRoute(token, hubRoutesId);
        return ResponseEntity.ok(ApiResponse.success("허브 경로 삭제 성공"));
    }
}
