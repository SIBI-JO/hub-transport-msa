package com.sibijo.product.service;

import com.sibijo.product.client.CompanyClient;
import com.sibijo.product.client.CompanyResponseDto;
import com.sibijo.product.dto.ProductRequest;
import com.sibijo.product.dto.ProductResponseDto;
import com.sibijo.product.entity.HubStock;
import com.sibijo.product.entity.Product;
import com.sibijo.product.repository.HubStockRepository;
import com.sibijo.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
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

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
    }

    @Transactional
    public Product createProduct(ProductRequest request) {
        // 회사 서비스에 등록된 hub 정보를 조회 (존재하지 않으면 예외 발생)
        CompanyResponseDto companyResponse = companyClient.getHubByCompanyId(request.getCompanyId());
        if (companyResponse == null || companyResponse.getHubId() == null) {
            throw new IllegalArgumentException("Company not found or hub info missing for companyId: " + request.getCompanyId());
        }
        Product product = Product.builder()
                .productName(request.getProductName())
                .price(request.getPrice())
                .companyId(request.getCompanyId())
                .build();
        Product savedProduct = productRepository.save(product);

        // 회사 서비스에서 받아온 hubId를 사용해 초기 재고 0 기록 생성
        HubStock hubStock = HubStock.builder()
                .hubId(companyResponse.getHubId())
                .companyId(request.getCompanyId())
                .product(savedProduct)
                .amount(0L)
                .build();
        hubStockRepository.save(hubStock);

        return savedProduct;
    }

    @Transactional
    public Product updateProduct(UUID productId, ProductRequest request) {
        Product existingProduct = getProductById(productId);
        // 업데이트 전에도 회사 검증 (필요시)
        CompanyResponseDto companyResponse = companyClient.getHubByCompanyId(request.getCompanyId());
        if (companyResponse == null || companyResponse.getHubId() == null) {
            throw new IllegalArgumentException("Company not found or hub info missing for companyId: " + request.getCompanyId());
        }
        existingProduct.setProductName(request.getProductName());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setCompanyId(request.getCompanyId());
        // 재고 기록의 hubId 업데이트는 별도 로직(필요 시)로 처리

        return productRepository.save(existingProduct);
    }

    public void deleteProduct(UUID productId) {
        productRepository.deleteById(productId);
    }

    /**
     * 주문 서비스가 호출할 API – 해당 상품의 재고와 연결된 허브 정보를 반환합니다.
     */
    public ProductResponseDto getProductOrderInfo(UUID productId) {
        Product product = getProductById(productId);
        HubStock hubStock = hubStockRepository.findByProduct(product)
                .orElseThrow(() -> new IllegalArgumentException("Hub stock not found for productId: " + productId));
        return ProductResponseDto.builder()
                .hubId(hubStock.getHubId())
                .amount(hubStock.getAmount())
                .build();
    }
}
