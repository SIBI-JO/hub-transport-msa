package com.sibijo.order.infrastructure.client.Product;

import com.sibijo.common.dto.ApiResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service")
public interface ProductClient {

    @GetMapping("/api/products/{productId}/order")
    ApiResponse<ProductResponseDto> getProductOrderInfo(@PathVariable("productId") UUID productId);

}
