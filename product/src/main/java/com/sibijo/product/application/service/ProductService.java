package com.sibijo.product.application.service;

import com.sibijo.common.dto.ApiResponse;
import com.sibijo.product.infrastructure.client.CompanyClient;
import com.sibijo.product.infrastructure.client.CompanyResponseDto;
import com.sibijo.product.presentation.dto.ProductRequest;
import com.sibijo.product.presentation.dto.ProductResponseDto;
import com.sibijo.product.domain.entity.HubStock;
import com.sibijo.product.domain.entity.Product;
import com.sibijo.product.domain.repository.HubStockRepository;
import com.sibijo.product.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final HubStockRepository hubStockRepository;
    private final CompanyClient companyClient;

    /**
     * 전체 상품 조회
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * 특정 상품 조회 (존재하지 않으면 null)
     */
    public Product getProductById(UUID productId) {
        return productRepository.findById(productId).orElse(null);
    }

    /**
     * 신규 상품 등록
     */
    @Transactional
    public Product createProduct(ProductRequest request) {
        // 회사 서비스에서 ApiResponse로 감싼 응답을 받아 data 필드를 추출합니다.
        ApiResponse<CompanyResponseDto> response = companyClient.getHubByCompanyId(request.getCompanyId());
        CompanyResponseDto companyResponse = response.getData();
        if (companyResponse == null || companyResponse.getHubId() == null) {
            throw new IllegalArgumentException("Company not found or hub info missing for companyId: " + request.getCompanyId());
        }

        // 상품 생성 및 저장
        Product product = new Product(
                request.getProductName(),
                request.getPrice(),
                request.getCompanyId()
        );
        Product savedProduct = productRepository.save(product);

        // 허브 재고 생성
        HubStock hubStock = new HubStock(
                companyResponse.getHubId(),
                request.getCompanyId(),
                savedProduct,
                0L
        );
        hubStockRepository.save(hubStock);

        return savedProduct;
    }

    /**
     * 기존 상품 정보 수정
     */
    @Transactional
    public Product updateProduct(UUID productId, ProductRequest request) {
        Product existingProduct = getProductById(productId);
        if (existingProduct == null) {
            throw new IllegalArgumentException("Product not found: " + productId);
        }

        ApiResponse<CompanyResponseDto> response = companyClient.getHubByCompanyId(request.getCompanyId());
        CompanyResponseDto companyResponse = response.getData();
        if (companyResponse == null || companyResponse.getHubId() == null) {
            throw new IllegalArgumentException("Company not found or hub info missing for companyId: " + request.getCompanyId());
        }

        existingProduct.setProductName(request.getProductName());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setCompanyId(request.getCompanyId());

        return productRepository.save(existingProduct);
    }

    /**
     * 상품 삭제 (Soft Delete)
     */
    @Transactional
    public Product deleteProduct(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
        productRepository.delete(product);
        return product;
    }

    /**
     * 주문 서비스가 호출할 API – 상품의 재고와 연결된 허브 정보를 반환
     */
    public ProductResponseDto getProductOrderInfo(UUID productId) {
        Product product = getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found: " + productId);
        }
        HubStock hubStock = hubStockRepository.findByProduct(product)
                .orElseThrow(() -> new IllegalArgumentException("Hub stock not found for productId: " + productId));

        return ProductResponseDto.builder()
                .hubId(hubStock.getHubId())
                .amount(hubStock.getAmount())
                .build();
    }

    // 검색 기능: 상품명, 가격 기준
    public Page<Product> searchProducts(String productName, Integer price, Pageable pageable) {
        return productRepository.searchProducts(productName, price, pageable);
    }
}
