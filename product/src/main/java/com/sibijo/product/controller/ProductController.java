package com.sibijo.product.controller;

import com.sibijo.product.dto.ProductRequest;
import com.sibijo.product.entity.Product;
import com.sibijo.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 전체 상품 조회 (페이징 없이 목록으로 조회)
     */
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    /**
     * 특정 상품의 상세 정보 조회
     */
    @GetMapping("/{productId}")
    public Product getProduct(@PathVariable Long productId) {
        return productService.getProductById(productId);
    }

    /**
     * 신규 상품 등록
     */
    @PostMapping
    public Product createProduct(@RequestBody ProductRequest request) {
        return productService.createProduct(request);
    }

    /**
     * 기존 상품 정보 수정
     */
    @PutMapping("/{productId}")
    public Product updateProduct(@PathVariable Long productId,
            @RequestBody ProductRequest request) {
        return productService.updateProduct(productId, request);
    }

    /**
     * 상품 삭제
     */
    @DeleteMapping("/{productId}")
    public void deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
    }
}
