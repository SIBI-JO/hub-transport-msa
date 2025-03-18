package com.sibijo.product.application.service;

import com.sibijo.product.presentation.dto.HubStockResponse;
import com.sibijo.product.domain.entity.HubStock;
import com.sibijo.product.domain.entity.Product;
import com.sibijo.product.domain.repository.HubStockRepository;
import com.sibijo.product.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HubStockService {

    private final HubStockRepository hubStockRepository;
    private final ProductRepository productRepository;

    /**
     * 상품 ID만으로 재고 업데이트
     */
    @Transactional
    public HubStockResponse updateStock(UUID productId, Long newAmount) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
        HubStock hubStock = hubStockRepository.findByProduct(product)
                .orElseThrow(() -> new IllegalArgumentException("Hub stock not found for product: " + productId));

        hubStock.setAmount(newAmount);
        hubStockRepository.save(hubStock);

        return HubStockResponse.builder()
                .hubStockId(hubStock.getHubStockId())
                .hubId(hubStock.getHubId())
                .companyId(hubStock.getCompanyId())
                .productId(product.getProductId())
                .amount(hubStock.getAmount())
                .build();
    }

    /**
     * 상품 ID만으로 재고 조회
     */
    public HubStockResponse getStock(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
        HubStock hubStock = hubStockRepository.findByProduct(product)
                .orElseThrow(() -> new IllegalArgumentException("Hub stock not found for product: " + productId));

        return HubStockResponse.builder()
                .hubStockId(hubStock.getHubStockId())
                .hubId(hubStock.getHubId())
                .companyId(hubStock.getCompanyId())
                .productId(product.getProductId())
                .amount(hubStock.getAmount())
                .build();
    }
}
