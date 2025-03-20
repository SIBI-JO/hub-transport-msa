package com.sibijo.user.presentation.controller;

import com.sibijo.common.dto.ApiResponse;
import com.sibijo.user.application.service.DeliveryAgentService;
import com.sibijo.user.presentation.dto.deliveryAgent.DeliveryAgentCreateRequestDto;
import com.sibijo.user.presentation.dto.deliveryAgent.DeliveryAgentCreateResponseDto;
import com.sibijo.user.presentation.dto.deliveryAgent.DeliveryAgentDeleteResponseDto;
import com.sibijo.user.presentation.dto.deliveryAgent.DeliveryAgentDetailsResponseDto;
import com.sibijo.user.presentation.dto.deliveryAgent.DeliveryAgentPageResponseDto;
import com.sibijo.user.presentation.dto.deliveryAgent.DeliveryAgentUpdateRequestDto;
import com.sibijo.user.presentation.dto.deliveryAgent.DeliveryAgentUpdateResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/delivery-agents")
@RequiredArgsConstructor
public class DeliveryAgentController {

    private final DeliveryAgentService deliveryAgentService;

    @GetMapping("/health-check")
    private ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok().body("delivery agents OK");
    }

    //CRUDS
    @PostMapping("")
    private ResponseEntity<ApiResponse<DeliveryAgentCreateResponseDto>> createDeliveryAgent(
            @RequestBody DeliveryAgentCreateRequestDto requestDto,
            HttpServletRequest request) {

        DeliveryAgentCreateResponseDto deliveryAgentCreateResponseDto = deliveryAgentService.createDeliveryAgent(requestDto, request);

        return ResponseEntity
                .created(URI.create("/delivery-agents" + deliveryAgentCreateResponseDto.getUserId()))
                .body(ApiResponse.success("성공", deliveryAgentCreateResponseDto));
    }

    @GetMapping("/{id}")
    private ResponseEntity<ApiResponse<DeliveryAgentDetailsResponseDto>> getDeliveryAgent(
            @PathVariable("id") Long id,
            HttpServletRequest request
    ) {
        DeliveryAgentDetailsResponseDto deliveryAgentDetailsResponseDto = deliveryAgentService.getDeliveryAgent(id, request);
        return ResponseEntity
                .ok(ApiResponse.success("success", deliveryAgentDetailsResponseDto));
    }

    @PatchMapping("/{id}")
    private ResponseEntity<ApiResponse<DeliveryAgentUpdateResponseDto>> updateDeliveryAgent(
            @PathVariable("id") Long id,
            @Valid @RequestBody DeliveryAgentUpdateRequestDto requestDto,
            BindingResult bindingResult,
            HttpServletRequest request
    ) {
        // validation 예외처리
        raiseValidationException(bindingResult);

        DeliveryAgentUpdateResponseDto deliveryAgentUpdateResponseDto = deliveryAgentService.updateDeliveryAgent(id, requestDto,
                request);
        return ResponseEntity
                .ok(ApiResponse.success("수정 성공", deliveryAgentUpdateResponseDto));
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<ApiResponse<DeliveryAgentDeleteResponseDto>> deleteDeliveryAgent(
            @PathVariable("id") Long id,
            HttpServletRequest request
    ) {
        DeliveryAgentDeleteResponseDto deliveryAgentDeleteResponseDto = deliveryAgentService.deleteDeliveryAgent(id, request);
        return ResponseEntity
                .ok(ApiResponse.success("삭제 성공", deliveryAgentDeleteResponseDto));
    }

    @GetMapping("")
    private ResponseEntity<ApiResponse<DeliveryAgentPageResponseDto>> searchDeliveryAgent(
            HttpServletRequest request,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size, //기본값 10
            @RequestParam(value = "orderby", defaultValue = "createdAt") String criteria,
            @RequestParam(value = "sort", defaultValue = "DESC") String sort,
            @RequestParam(value = "deliveryType", required = false) String deliveryType
    ) {
        DeliveryAgentPageResponseDto deliveryAgentPageResponseDto = deliveryAgentService.searchDeliveryAgents(request, page,
                size, criteria, sort, deliveryType);
        return ResponseEntity
                .ok(ApiResponse.success("검색 성공", deliveryAgentPageResponseDto));
    }

    // internal API
    @GetMapping("/hub")
    private ResponseEntity<ApiResponse<Long>> getHubDeliveryAgent() {
        // 배송 담당자 배정
        Long deliveryAgentId = deliveryAgentService.assignHubDeliveryAgent();

        return ResponseEntity
                .ok(ApiResponse.success("배송 담당자 지정 성공", deliveryAgentId));
    }

    @GetMapping("/company/{hubId}")
    private ResponseEntity<ApiResponse<Long>> getCompanyDeliveryAgent(@PathVariable UUID hubId) {
        // 배송 담당자 배정
        Long deliveryAgentId = deliveryAgentService.assignCompanyDeliveryAgent(hubId);

        return ResponseEntity
                .ok(ApiResponse.success("배송 담당자 지정 성공", deliveryAgentId));
    }


    private static void raiseValidationException(BindingResult bindingResult) {
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if (fieldErrors.size() > 0) {
            for (FieldError fieldError : fieldErrors) {
                throw new IllegalArgumentException(fieldError.getDefaultMessage());
            }
        }
    }

}
