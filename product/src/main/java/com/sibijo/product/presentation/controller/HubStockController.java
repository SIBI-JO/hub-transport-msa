package com.sibijo.product.presentation.controller;

import com.sibijo.common.dto.ApiResponse;
import com.sibijo.product.presentation.dto.HubStockResponse;
import com.sibijo.product.presentation.dto.UpdateStockRequest;
import com.sibijo.product.application.service.HubStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/hub-stocks")
@RequiredArgsConstructor
public class HubStockController {

    private final HubStockService hubStockService;

    // 상품 ID만으로 재고 조회
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<HubStockResponse>> getStock(@PathVariable UUID productId) {
        HubStockResponse response = hubStockService.getStock(productId);
        return ResponseEntity.ok(ApiResponse.success("허브 재고 조회 성공", response));
    }

    // 상품 ID만으로 재고 업데이트 (PATCH → PUT)
    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<HubStockResponse>> updateStock(@PathVariable("productId") UUID productId,
            @RequestBody UpdateStockRequest request) {
        HubStockResponse response = hubStockService.updateStock(productId, request.getNewAmount());
        return ResponseEntity.ok(ApiResponse.success("허브 재고 업데이트 성공", response));
    }
}
