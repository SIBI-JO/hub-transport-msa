package com.sibijo.product.presentation.controller;

import com.sibijo.common.dto.ApiResponse;
import com.sibijo.product.presentation.dto.ProductRequest;
import com.sibijo.product.presentation.dto.ProductResponseDto;
import com.sibijo.product.domain.entity.Product;
import com.sibijo.product.application.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sibijo.common.utils.Auth.JwtUtil;
import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success("전체 상품 조회 성공", products));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<Product>> getProduct(@PathVariable UUID productId) {
        Product product = productService.getProductById(productId);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.exception("상품을 찾을 수 없습니다.", null));
        }
        return ResponseEntity.ok(ApiResponse.success("상품 조회 성공", product));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Product>> createProduct(HttpServletRequest request, @RequestBody ProductRequest req) {
        String token = jwtUtil.extractToken(request);
        Product created = productService.createProduct(req, token);
        return ResponseEntity.ok(ApiResponse.success("상품 등록 성공", created));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(@PathVariable("productId") UUID productId, HttpServletRequest request, @RequestBody ProductRequest req) {
        String token = jwtUtil.extractToken(request);
        Product updated = productService.updateProduct(productId, req, token);
        return ResponseEntity.ok(ApiResponse.success("상품 수정 성공", updated));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Product>> deleteProduct(@PathVariable UUID productId, HttpServletRequest request) {
        String token = jwtUtil.extractToken(request);
        Product deleted = productService.deleteProduct(productId, token);
        return ResponseEntity.ok(ApiResponse.success("상품 삭제 성공", deleted));
    }

    @GetMapping("/{productId}/order")
    public ResponseEntity<ApiResponse<ProductResponseDto>> getProductOrderInfo(@PathVariable("productId") UUID productId) {
        ProductResponseDto dto = productService.getProductOrderInfo(productId);
        return ResponseEntity.ok(ApiResponse.success("주문용 상품 재고/허브 정보 조회 성공", dto));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<Product>>> searchProducts(
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) Integer price,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortDirection) {

        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }
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
