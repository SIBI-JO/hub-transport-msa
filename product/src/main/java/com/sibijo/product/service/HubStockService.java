package com.sibijo.product.service;

import com.sibijo.product.dto.HubStockResponse;
import com.sibijo.product.entity.HubStock;
import com.sibijo.product.entity.Product;
import com.sibijo.product.repository.HubStockRepository;
import com.sibijo.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HubStockService {

    private final HubStockRepository hubStockRepository;
    private final ProductRepository productRepository;

    @Transactional
    public HubStockResponse updateStock(UUID productId, UUID hubId, Long newAmount) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
        HubStock hubStock = hubStockRepository.findByProductAndHubId(product, hubId)
                .orElseThrow(() -> new IllegalArgumentException("Hub stock not found for hubId: " + hubId));
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

    public HubStockResponse getStock(UUID productId, UUID hubId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
        HubStock hubStock = hubStockRepository.findByProductAndHubId(product, hubId)
                .orElseThrow(() -> new IllegalArgumentException("Hub stock not found for hubId: " + hubId));
        return HubStockResponse.builder()
                .hubStockId(hubStock.getHubStockId())
                .hubId(hubStock.getHubId())
                .companyId(hubStock.getCompanyId())
                .productId(product.getProductId())
                .amount(hubStock.getAmount())
                .build();
    }
}
