package com.sibijo.delivery.infrastructure.client.product;

import com.sibijo.common.dto.ApiResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "product-service")
public interface ProductClient {

    @PatchMapping("/api/hub-stocks/{productId}")
    ApiResponse<HubStockResponse> updateStock(@PathVariable("productId") UUID productId,
            @RequestBody UpdateStockRequest request);

}
