package com.sibijo.product.infrastructure.client;

import com.sibijo.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.UUID;

@FeignClient(name = "company-service")
public interface CompanyClient {


    @GetMapping("/api/companies/{companyId}/exists")
    ApiResponse<Boolean> companyExists(@PathVariable("companyId") UUID companyId);

    @GetMapping("/api/companies/{companyId}/order")
    ApiResponse<CompanyResponseDto> getHubByCompanyId(@PathVariable("companyId") UUID companyId);
}
