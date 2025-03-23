package com.sibijo.ai.infrastructure.client.product;

import com.sibijo.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.UUID;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "product-service")
public interface ProductServiceClient {

    @GetMapping("/api/products/{productId}")
    ApiResponse<ProductDetailsDto> getProductDetails(
            @PathVariable("productId") UUID productId,
            @RequestHeader("Authorization") String bearerToken
    );
}
