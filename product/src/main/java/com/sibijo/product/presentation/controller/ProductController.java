package com.sibijo.product.presentation.controller;

import com.sibijo.common.dto.ApiResponse;
import com.sibijo.product.presentation.dto.ProductRequest;
import com.sibijo.product.presentation.dto.ProductResponseDto;
import com.sibijo.product.domain.entity.Product;
import com.sibijo.product.application.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 전체 상품 조회
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success("전체 상품 조회 성공", products));
    }

    /**
     * 특정 상품 조회
     */
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<Product>> getProduct(@PathVariable UUID productId) {
        Product product = productService.getProductById(productId);
        if (product == null) {
            // 404 응답
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.exception("상품을 찾을 수 없습니다.", null));
        }
        return ResponseEntity.ok(ApiResponse.success("상품 조회 성공", product));
    }

    /**
     * 신규 상품 등록
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Product>> createProduct(@RequestBody ProductRequest request) {
        Product created = productService.createProduct(request);
        return ResponseEntity.ok(ApiResponse.success("상품 등록 성공", created));
    }

    /**
     * 기존 상품 정보 수정
     */
    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(@PathVariable UUID productId,
            @RequestBody ProductRequest request) {
        Product updated = productService.updateProduct(productId, request);
        return ResponseEntity.ok(ApiResponse.success("상품 수정 성공", updated));
    }

    /**
     * 상품 삭제
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable UUID productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok(ApiResponse.success("상품 삭제 성공", null));
    }

    /**
     * 주문 서비스에서 호출할 API – 상품의 재고 정보와 연결된 허브 ID 반환
     */
    @GetMapping("/{productId}/order")
    public ResponseEntity<ApiResponse<ProductResponseDto>> getProductOrderInfo(@PathVariable UUID productId) {
        ProductResponseDto dto = productService.getProductOrderInfo(productId);
        return ResponseEntity.ok(ApiResponse.success("주문용 상품 재고/허브 정보 조회 성공", dto));
    }
}
