package com.sibijo.order.infrastructure.client.Product;

import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service")
public interface ProductClient {

    @GetMapping("/api/products/{productId}/order")
    ProductResponseDto getProductStock(@PathVariable("productId") UUID productId);

}
