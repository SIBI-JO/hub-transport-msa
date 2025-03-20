package com.sibijo.user.infrastructure.client.company;

import com.sibijo.common.dto.ApiResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "company-service", contextId = "companyClient")
public interface CompanyClient {

    @GetMapping("/internal/companies/{companyId}")
    ApiResponse<CompanyResponseDto> getCompanyHubByCompanyId(@PathVariable("companyId") UUID companyId);

}
