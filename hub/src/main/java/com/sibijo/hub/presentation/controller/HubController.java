package com.sibijo.hub.presentation.controller;

import com.sibijo.common.dto.ApiResponse;
import com.sibijo.common.utils.Auth.JwtUtil;
import com.sibijo.common.utils.page.PageableUtils;
import com.sibijo.hub.application.service.HubApplicationService;
import com.sibijo.hub.presentation.dto.HubRequestDto;
import com.sibijo.hub.presentation.dto.HubResponseDto;
import com.sibijo.hub.presentation.dto.HubToRouteDto;
import com.sibijo.hub.presentation.dto.HubUpdateRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/hubs")
@RequiredArgsConstructor
public class HubController {

    private final HubApplicationService hubApplicationService;
    private final JwtUtil jwtUtil;

    //회원가입 시 허브 아이디 존재 확인
    @GetMapping("/{hubId}/exists")
    public ResponseEntity<ApiResponse<Boolean>> hubExists(@PathVariable("hubId") UUID hubId) {
        boolean exists = hubApplicationService.isHubExists(hubId);
        return ResponseEntity.ok(ApiResponse.success("허브 존재 확인 성공", exists));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<HubResponseDto>> createHub(
            @Valid @RequestBody HubRequestDto hubRequestDto,
            HttpServletRequest request
    ) {
        String token = jwtUtil.extractToken(request);
        log.info("token: {}", token);

        HubResponseDto hubResponseDto = hubApplicationService.createHub(
                hubRequestDto
        );

        return ResponseEntity.ok(ApiResponse.success("허브 생성 성공", hubResponseDto));

    }

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
                    defaultValue = "중앙허브",
                    required = false
            ) String hubTypeName,
            @PageableDefault(
                    size = 10,
                    page = 1,
                    sort = {"createdAt", "updatedAt"},
                    direction = Direction.ASC
            ) Pageable pageable
    ) {
        System.out.println("page: " + pageable);
        Pageable validatedPageable = PageableUtils.validatePageable(pageable);
        System.out.println("pageable: " + validatedPageable);
        Page<HubResponseDto> searchHubs = hubApplicationService.searchHubs(
                hubName,
                hubLocation,
                hubTypeName,
                validatedPageable
        );

        return ResponseEntity.ok(ApiResponse.success("허브 검색 성공", searchHubs));
    }

    @GetMapping("/{hubId}")
    public ResponseEntity<ApiResponse<HubResponseDto>> getHub(@PathVariable("hubId") UUID hubId) {
        HubResponseDto hubResponseDto = hubApplicationService.getHub(hubId);

        return ResponseEntity.ok(ApiResponse.success("허브 단일 조회 성공", hubResponseDto));
    }

    @GetMapping("/hub-routes/{hubId}")
    public HubToRouteDto getHubForHubRoutes(@PathVariable("hubId") UUID hubId) {
        HubToRouteDto hubToRouteDto = hubApplicationService.getHubForHubRoutes(hubId);
        log.info("hubToRouteDto: " + hubToRouteDto);
        return hubToRouteDto;
    }

    @PatchMapping("/{hubId}")
    public ResponseEntity<ApiResponse<HubResponseDto>> updateHub(
            @PathVariable UUID hubId,
            @RequestBody HubUpdateRequestDto hubUpdateRequestDto) {
        HubResponseDto hubResponseDto = hubApplicationService.updateHub(hubId, hubUpdateRequestDto);
        return ResponseEntity.ok(ApiResponse.success("허브 수정 성공", hubResponseDto));
    }

    @DeleteMapping("/{hubId}")
    public ResponseEntity<ApiResponse<?>> deleteHub(@PathVariable("hubId") UUID hubId) {
        hubApplicationService.deleteHub(hubId);
        return ResponseEntity.ok(ApiResponse.success("허브 삭제 성공"));
    }
}
