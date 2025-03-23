package com.sibijo.delivery.infrastructure.client.company;

import com.sibijo.common.dto.ApiResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "company-service")
public interface CompanyClient {

    @GetMapping("/api/companies/{companyId}/order")
    ApiResponse<CompanyResponseDto> getCompanyOrderInfo(@PathVariable UUID companyId);
}