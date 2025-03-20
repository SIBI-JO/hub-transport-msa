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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.UUID;
import java.util.List;

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
     * 상품 삭제 (Soft Delete)
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Product>> deleteProduct(@PathVariable UUID productId) {
        Product deletedProduct = productService.deleteProduct(productId);
        return ResponseEntity.ok(ApiResponse.success("상품 삭제 성공", deletedProduct));
    }

    /**
     * 주문 서비스에서 호출할 API – 상품의 재고 정보와 연결된 허브 ID 반환
     */
    @GetMapping("/{productId}/order")
    public ResponseEntity<ApiResponse<ProductResponseDto>> getProductOrderInfo(@PathVariable UUID productId) {
        ProductResponseDto dto = productService.getProductOrderInfo(productId);
        return ResponseEntity.ok(ApiResponse.success("주문용 상품 재고/허브 정보 조회 성공", dto));
    }

    // 검색 엔드포인트: 예) /api/products/search?productName=abc&price=10000
    // 페이지 크기는 10, 30, 50건만 허용하며, 정렬 기본값은 createdAt, updatedAt 기준
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<Product>>> searchProducts(
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) Integer price,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortDirection) {

        // 페이지 크기 제한: 오직 10, 30, 50만 허용
        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }

        // 정렬: 별도 파라미터 없으면 기본적으로 createdAt, updatedAt 기준 정렬
        Sort sort = Sort.by("createdAt").ascending().and(Sort.by("updatedAt").ascending());
        if (sortField != null && !sortField.isEmpty()) {
            Sort.Direction direction = Sort.Direction.ASC;
            if (sortDirection != null && sortDirection.equalsIgnoreCase("desc")) {
                direction = Sort.Direction.DESC;
            }
            sort = Sort.by(direction, sortField);
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> products = productService.searchProducts(productName, price, pageable);
        return ResponseEntity.ok(ApiResponse.success("검색 결과", products));
    }
}

