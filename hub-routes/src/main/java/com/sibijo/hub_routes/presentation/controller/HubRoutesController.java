package com.sibijo.hub_routes.presentation.controller;

import com.sibijo.common.dto.ApiResponse;
import com.sibijo.common.utils.page.PageableUtils;
import com.sibijo.hub_routes.application.service.HubRoutesApplicationService;
import com.sibijo.hub_routes.presentation.dto.HubRoutesRequestDto;
import com.sibijo.hub_routes.presentation.dto.HubRoutesResponseDto;
import com.sibijo.hub_routes.presentation.dto.HubRoutesUpdateRequestDto;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hub-routes")
@RequiredArgsConstructor
public class HubRoutesController {

    private final HubRoutesApplicationService hubRoutesApplicationService;

    @PostMapping
    public ResponseEntity<ApiResponse<HubRoutesResponseDto>> createHubRoutes(
            @Valid @RequestBody HubRoutesRequestDto hubRoutesRequestDto
    ) {
        HubRoutesResponseDto hubRoutesResponseDto = hubRoutesApplicationService.createHubRoutes(
                hubRoutesRequestDto
        );

        return ResponseEntity.ok(ApiResponse.success("허브 경로 생성 성공", hubRoutesResponseDto));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<HubRoutesResponseDto>>> searchHubRoutes(
            @PageableDefault(
                    size = 10,
                    page = 1,
                    sort = {"createdAt", "updatedAt"},
                    direction = Direction.ASC
            ) Pageable pageable
    ) {
        Pageable validatedPageable = PageableUtils.validatePageable(pageable);
        Page<HubRoutesResponseDto> searchedHubRoutes = hubRoutesApplicationService.searchHubRoutes(
                validatedPageable
        );

        return ResponseEntity.ok(ApiResponse.success("허브 경로 검색 성공", searchedHubRoutes));
    }

    @GetMapping("/{hubRoutesId}")
    public ResponseEntity<ApiResponse<HubRoutesResponseDto>> getHubRoute(
            @PathVariable("hubRoutesId") UUID hubRoutesId) {
        HubRoutesResponseDto hubRoutesResponseDto = hubRoutesApplicationService.getHubRoute(
                hubRoutesId);

        return ResponseEntity.ok(ApiResponse.success("허브 경로 단일 조회 성공", hubRoutesResponseDto));
    }

    @PatchMapping("/{hubRoutesId}")
    public ResponseEntity<ApiResponse<HubRoutesResponseDto>> updateHubRoute(
            @PathVariable("hubRoutesId") UUID hubRoutesId,
            @RequestBody HubRoutesUpdateRequestDto hubRoutesUpdateRequestDto
    ) {
        HubRoutesResponseDto hubRoutesResponseDto = hubRoutesApplicationService.updateHubRoutes(
                hubRoutesId, hubRoutesUpdateRequestDto);

        return ResponseEntity.ok(ApiResponse.success("허브 경로 수정 성공", hubRoutesResponseDto));
    }

    @DeleteMapping("/{hubRoutesId}")
    public ResponseEntity<ApiResponse<HubRoutesResponseDto>> deleteHubRoute(
            @PathVariable("hubRoutesId") UUID hubRoutesId
    ) {
        hubRoutesApplicationService.deleteHubRoute(hubRoutesId);
        return ResponseEntity.ok(ApiResponse.success("허브 경로 삭제 성공"));
    }
}
