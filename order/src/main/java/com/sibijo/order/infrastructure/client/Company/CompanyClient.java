package com.sibijo.order.infrastructure.client.Company;

import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "company-service")
public interface CompanyClient {

    @GetMapping("/api/companies/{companyId}/order")
    CompanyResponseDto getHubByCompanyId(@PathVariable UUID companyId);
}
