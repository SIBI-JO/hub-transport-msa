package com.sibijo.product.service;

import com.sibijo.product.client.CompanyClient;
import com.sibijo.product.dto.ProductRequest;
import com.sibijo.product.entity.Product;
import com.sibijo.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CompanyClient companyClient;

    /**
     * 전체 상품 조회 (페이징 없이)
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * 특정 상품 조회
     */
    public Product getProductById(Long productId) {
        return productRepository.findById(productId).orElse(null);
    }

    /**
     * 신규 상품 등록 (회사 존재 여부 검증)
     */
    public Product createProduct(ProductRequest request) {
        if (!companyClient.existsCompany(request.getCompanyId())) {
            throw new IllegalArgumentException("회사 정보가 존재하지 않습니다. companyId: " + request.getCompanyId());
        }
        Product product = Product.builder()
                .productName(request.getProductName())
                .price(request.getPrice())
                .companyId(request.getCompanyId())
                .hubId(request.getHubId())
                .build();
        return productRepository.save(product);
    }

    /**
     * 기존 상품 정보 수정
     */
    public Product updateProduct(Long productId, ProductRequest request) {
        Product existingProduct = getProductById(productId);
        if(existingProduct == null) {
            throw new IllegalArgumentException("상품을 찾을 수 없습니다. productId: " + productId);
        }
        if (!companyClient.existsCompany(request.getCompanyId())) {
            throw new IllegalArgumentException("회사 정보가 존재하지 않습니다. companyId: " + request.getCompanyId());
        }
        existingProduct.setProductName(request.getProductName());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setCompanyId(request.getCompanyId());
        existingProduct.setHubId(request.getHubId());
        return productRepository.save(existingProduct);
    }

    /**
     * 상품 삭제
     */
    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }
}
