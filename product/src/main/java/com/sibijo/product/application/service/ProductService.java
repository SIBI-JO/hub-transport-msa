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
import com.sibijo.common.exception.CustomException;
import com.sibijo.common.exception.codes.CommonExceptionCode;
import com.sibijo.common.utils.Auth.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final HubStockRepository hubStockRepository;
    private final CompanyClient companyClient;
    private final JwtUtil jwtUtil; // JWT 토큰을 통한 권한 체크

    /**
     * 전체 상품 조회 (읽기 권한 모두 허용)
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * 특정 상품 조회 (읽기 권한 모두 허용)
     */
    public Product getProductById(UUID productId) {
        return productRepository.findById(productId).orElse(null);
    }

    /**
     * 신규 상품 등록
     * 권한: MASTER, HUB_ADMIN만 가능
     * - HUB_ADMIN의 경우 자신의 허브 소속 업체에 한정 (CompanyClient를 통해 검증)
     */
    @Transactional
    public Product createProduct(ProductRequest request, String token) {
        String role = jwtUtil.extractRole(token);
        UUID tokenHubId = jwtUtil.extractHubIdForOrder(token);

        // 업체 존재 여부 검증 (신규 엔드포인트 사용)
        ApiResponse<Boolean> existsResponse = companyClient.companyExists(request.getCompanyId());
        if (existsResponse.getData() == null || !existsResponse.getData()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 업체ID 입니다.");
        }

        if ("MASTER".equals(role)) {
            // 허용
        } else if ("HUB".equals(role) || "HUB_ADMIN".equals(role)) {
            ApiResponse<CompanyResponseDto> hubResponse = companyClient.getHubByCompanyId(request.getCompanyId());
            CompanyResponseDto companyResponse = hubResponse.getData();
            if (companyResponse == null || companyResponse.getHubId() == null ||
                    !companyResponse.getHubId().equals(tokenHubId)) {
                throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
            }
        } else {
            throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
        }

        // 상품 생성
        Product product = new Product(
                request.getProductName(),
                request.getPrice(),
                request.getCompanyId()
        );
        Product savedProduct = productRepository.save(product);

        // 허브 재고 생성
        ApiResponse<CompanyResponseDto> hubResponse = companyClient.getHubByCompanyId(request.getCompanyId());
        CompanyResponseDto companyResponse = hubResponse.getData();
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
     * 권한:
     * - MASTER, HUB_ADMIN: 수정 가능 (단, HUB_ADMIN은 자신의 허브 소속 업체만)
     * - COMPANY: 본인의 상품만 수정 가능
     */
    @Transactional
    public Product updateProduct(UUID productId, ProductRequest request, String token) {
        Product existingProduct = getProductById(productId);
        if (existingProduct == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + productId);
        }

        // 업체 존재 여부 검증 (수정 시 변경된 업체 ID에 대해)
        if (!existingProduct.getCompanyId().equals(request.getCompanyId())) {
            ApiResponse<Boolean> existsResponse = companyClient.companyExists(request.getCompanyId());
            if (existsResponse.getData() == null || !existsResponse.getData()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 업체ID 입니다.");
            }
        }

        String role = jwtUtil.extractRole(token);
        UUID tokenHubId = jwtUtil.extractHubId(token);
        UUID tokenCompanyId = jwtUtil.extractCompanyId(token);

        if ("MASTER".equals(role)) {
            // 허용
        } else if ("HUB".equals(role) || "HUB_ADMIN".equals(role)) {
            ApiResponse<CompanyResponseDto> hubResponse = companyClient.getHubByCompanyId(request.getCompanyId());
            CompanyResponseDto companyResponse = hubResponse.getData();
            if (companyResponse == null || companyResponse.getHubId() == null ||
                    !companyResponse.getHubId().equals(tokenHubId)) {
                throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
            }
        } else if ("COMPANY".equals(role)) {
            if (!existingProduct.getCompanyId().equals(tokenCompanyId)) {
                throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
            }
        } else {
            throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
        }

        existingProduct.setProductName(request.getProductName());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setCompanyId(request.getCompanyId());

        return productRepository.save(existingProduct);
    }

    /**
     * 상품 삭제 (Soft Delete)
     * 권한: MASTER, HUB_ADMIN만 가능
     */
    @Transactional
    public Product deleteProduct(UUID productId, String token) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
        String role = jwtUtil.extractRole(token);
        UUID tokenHubId = jwtUtil.extractHubId(token);

        if ("MASTER".equals(role)) {
            // 허용
        } else if ("HUB".equals(role) || "HUB_ADMIN".equals(role)) {
            ApiResponse<CompanyResponseDto> hubResponse = companyClient.getHubByCompanyId(product.getCompanyId());
            CompanyResponseDto companyResponse = hubResponse.getData();
            if (companyResponse == null || companyResponse.getHubId() == null ||
                    !companyResponse.getHubId().equals(tokenHubId)) {
                throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
            }
        } else {
            throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
        }

        productRepository.delete(product);
        return product;
    }

    /**
     * 주문 서비스가 호출할 API – 상품의 재고와 연결된 허브 정보를 반환 (읽기 권한 모두 허용)
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

    // 검색 기능: 상품명, 가격 기준 (읽기 허용)
    public Page<Product> searchProducts(String productName, Integer price, Pageable pageable) {
        return productRepository.searchProducts(productName, price, pageable);
    }
}
