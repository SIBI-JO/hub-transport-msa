package com.sibijo.delivery.presentation.controller;

import com.sibijo.common.dto.ApiResponse;
import com.sibijo.common.exception.CustomException;
import com.sibijo.common.exception.codes.CommonExceptionCode;
import com.sibijo.common.utils.Auth.JwtUtil;
import com.sibijo.delivery.application.dto.DeliveryResponseDto;
import com.sibijo.delivery.application.dto.DeliveryRouteResponseDto;
import com.sibijo.delivery.application.service.CustomDeliveryService;
import com.sibijo.delivery.domain.enums.DeliveryStatusEnum;
import com.sibijo.delivery.presentation.dto.DeliveryRouteUpdateRequestDto;
import com.sibijo.delivery.presentation.dto.DeliveryUpdateRequestDto;
import com.sibijo.delivery.presentation.dto.OrderToDeliveryRequestDto;
import com.sibijo.delivery.presentation.dto.StockInfomationDto;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j(topic = "배송 Controller")
@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final JwtUtil jwtUtil;
    private final CustomDeliveryService deliveryService;

    /**
     *  배송 및 배송 경로 생성
     *  권한 : 주문 생성 시 자동 생성 -> ALL
     */
    @PostMapping
    public void createDelivery(
            @RequestBody OrderToDeliveryRequestDto requestDto) {

        deliveryService.createDelivery(requestDto);
//        return ResponseEntity.ok();
    }


    /**
     *  배송 전체 조회
     *  권한 : Hub_Manager -> 자신의 허브만    //   Delivery_Manager -> 자신의 배송만
     *                                      // Company_Manager -> 자신의 업체만
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<DeliveryResponseDto>>> getDeliveries(
            @RequestParam(required = false) String deliveryAddress,
            @RequestParam(required = false) String receiver,
            HttpServletRequest request,
            @PageableDefault(size = 10, page = 1, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        String token = jwtUtil.extractToken(request);
        return ResponseEntity.ok(ApiResponse.success("배송 전체 조회 성공", deliveryService.getDeliveries(token, receiver, deliveryAddress, pageable)));
    }



    /**
     *  배송 상세 조회
     *  권한 : Hub_Manager -> 자신의 허브만    //   Delivery_Manager -> 자신의 배송만
     *                                      // Company_Manager -> 자신의 업체만
     */
    @GetMapping("/{deliveryId}")
    public ResponseEntity<ApiResponse<DeliveryResponseDto>> getDeliveryDetails(@PathVariable UUID deliveryId,
            HttpServletRequest request) {
        String token = jwtUtil.extractToken(request);
        return ResponseEntity.ok(ApiResponse.success("배송 상세 조회 성공", deliveryService.getDeliveryDetails(deliveryId, token)));
    }



    /**
     *  배송 수정
     *  권한 : Hub_Manager -> 자신의 허브만    //   Delivery_Manager -> 자신의 배송만
     *  업체 배송 담당자 수정을 어떻게 처리해야하는가
     *  -> 배송을 만들 때 유저 서버로 업체ID를 보내서 업체 관계자 정보 받아서 넣어야 하나?
     */
    @PutMapping("/{deliveryId}")
    public ResponseEntity<ApiResponse<DeliveryResponseDto>> updateDelivery(@PathVariable UUID deliveryId,
            @RequestBody DeliveryUpdateRequestDto requestDto,
            HttpServletRequest request) {
        String token = jwtUtil.extractToken(request);
        return ResponseEntity.ok(ApiResponse.success("배송 수정 성공", deliveryService.updateDelivery(deliveryId, requestDto, token)));
    }



    /**
     *  배송 취소
     *  권한 : Hub_Manager -> 자신의 허브만
     */
    @DeleteMapping("/{deliveryId}")
    public ResponseEntity<ApiResponse<DeliveryResponseDto>> deleteDelivery(
            @PathVariable UUID deliveryId,
            HttpServletRequest request) {
        String token = jwtUtil.extractToken(request);
        return ResponseEntity.ok(ApiResponse.success("배송 삭제 성공", deliveryService.deleteDelivery(deliveryId, token)));
    }

    /**
     *  배송 상태 변경 & 배송 담당자 변경
     */
    @PatchMapping("/{deliveryId}/status")
    public ResponseEntity<ApiResponse<String>> updateDeliveryStatus(
            @PathVariable UUID deliveryId,
            HttpServletRequest request
    ) {
        String token = jwtUtil.extractToken(request);
        deliveryService.updateDeliveryStatus(deliveryId, token);
        return ResponseEntity.ok(ApiResponse.success("배송 상태 변경 성공", ""));
    }


    /*******************************************************************
     *  배송 경로 전체 조회
     *  권한 : Hub_Manager -> 자신의 허브만    //   Delivery_Manager -> 자신의 배송만
     *                                      // Company_Manager -> 자신의 업체만
     */
    @GetMapping("/routes")
    public ResponseEntity<ApiResponse<Page<DeliveryRouteResponseDto>>> getDeliveryRoutes(
            @RequestParam(required = false) UUID recipientsId,
            @RequestParam(required = false) Long deliveryManagerId,
            HttpServletRequest request,
            @PageableDefault(size = 10, page = 1, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        String token = jwtUtil.extractToken(request);
        return ResponseEntity.ok(ApiResponse.success("배송 경로 전체 조회 성공", deliveryService.getDeliveryRoutes(token, recipientsId, deliveryManagerId, pageable)));
    }



    /**
     *  배송 경로 상세 조회
     *  권한 : Hub_Manager -> 자신의 허브만    // Delivery_Manager -> 자신의 배송만
     *                                      // Company_Manager -> 자신의 업체만
     */
    @GetMapping("/routes/{routeId}")
    public ResponseEntity<ApiResponse<DeliveryRouteResponseDto>> getDeliveryRouteDetails(@PathVariable UUID routeId,
            HttpServletRequest request) {
        String token = jwtUtil.extractToken(request);
        return ResponseEntity.ok(ApiResponse.success("배송 경로 상세 조회 성공", deliveryService.getDeliveryRouteDetails(routeId, token)));
    }



    /**
     *  배송 경로 수정
     *  권한 : Hub_Manager -> 자신의 허브만    //   Delivery_Manager -> 자신의 배송만
     *  실제 거리 / 실제 소요 시간만 수정 가능? 아니면 다른 부분도 수정 가눙?
     */
    @PutMapping("/routes/{routeId}")
    public ResponseEntity<ApiResponse<DeliveryRouteResponseDto>> updateDeliveryRoute(@PathVariable UUID routeId,
            @RequestBody DeliveryRouteUpdateRequestDto requestDto,
            HttpServletRequest request) {
        String token = jwtUtil.extractToken(request);
        return ResponseEntity.ok(ApiResponse.success("배송 경로 수정 성공", deliveryService.updateDeliveryRoute(routeId, requestDto, token)));
    }



    /**
     *  배송 경로 삭제
     *  권한 : Hub_Manager -> 자신의 허브만
     */
    @DeleteMapping("/routes/{routeId}")
    public ResponseEntity<ApiResponse<DeliveryRouteResponseDto>> deleteDeliveryRoute(
            @PathVariable UUID routeId,
            HttpServletRequest request) {
        String token = jwtUtil.extractToken(request);
        return ResponseEntity.ok(ApiResponse.success("배송 삭제 성공", deliveryService.deleteDeliveryRoute(routeId, token)));
    }

    /**
     *  배송 경로 배송ID로 조회
     */
    @GetMapping("/routes/delivery/{deliveryId}")
    public ResponseEntity<ApiResponse<DeliveryRouteResponseDto>> getDeliveryRouteForAi(
            @PathVariable UUID deliveryId
    ) {
        return ResponseEntity.ok(ApiResponse.success("배송 경로 정보 조회 성공", deliveryService.getDeliveryRouteForAi(deliveryId)));
    }

}
