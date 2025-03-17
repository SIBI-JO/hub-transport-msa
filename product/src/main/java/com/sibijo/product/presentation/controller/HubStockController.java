// 수정된 HubStockController
package com.sibijo.product.presentation.controller;

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

    @GetMapping("/{productId}/{hubId}")
    public ResponseEntity<HubStockResponse> getStock(@PathVariable UUID productId,
            @PathVariable UUID hubId) {
        return ResponseEntity.ok(hubStockService.getStock(productId, hubId));
    }

    // JSON 바디를 사용하는 재고 수정 API
    @PatchMapping("/{productId}/{hubId}")
    public ResponseEntity<HubStockResponse> updateStock(@PathVariable UUID productId,
            @PathVariable UUID hubId,
            @RequestBody UpdateStockRequest request) {
        return ResponseEntity.ok(hubStockService.updateStock(productId, hubId, request.getNewAmount()));
    }
}
