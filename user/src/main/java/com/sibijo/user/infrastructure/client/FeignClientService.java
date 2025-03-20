package com.sibijo.user.infrastructure.client;

import com.sibijo.common.dto.ApiResponse;
import com.sibijo.user.application.dto.HubResponseDto;
import com.sibijo.user.infrastructure.client.company.CompanyClient;
import com.sibijo.user.infrastructure.client.company.CompanyResponseDto;
import com.sibijo.user.infrastructure.client.hub.HubClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeignClientService {

    private final CompanyClient companyClient;
    private final HubClient hubClient;

    // Company Feign Client Fetch
    @CircuitBreaker(name = "companyService", fallbackMethod = "fallbackCompanyFeignClient")
    public UUID CallCompanyFeignClient(UUID companyId) {
        ApiResponse<CompanyResponseDto> company = companyClient.getCompanyHubByCompanyId(companyId);
        if (company.getData() == null) {
            return null;
        }
        log.info("feign client test: {}, {}", company.getMessage(), company.getData().getHubId());
        return company.getData().getHubId();
    }

    // Hub Feign Client Fetch
    @CircuitBreaker(name = "hubService", fallbackMethod = "fallbackHubFeignClient")
    public HubResponseDto CallHubFeignClient(UUID hubId) {
        ApiResponse hub = hubClient.hubExists(hubId);
        log.info("feign client test: {}, {}", hub.getMessage(), hub.getData());
        return HubResponseDto.builder()
                .hubId(hubId)
                .hubStatus((Boolean) hub.getData())
                .build();
    }

    // Fallback 메서드 (Company)
    public UUID fallbackCompanyFeignClient(UUID companyId, Throwable t) {
        log.error("Company Feign Client 호출 실패 (Fallback 처리): {}", t.getMessage());
        return null;  // 기본값 반환
    }

    // Fallback 메서드 (Hub)
    public HubResponseDto fallbackHubFeignClient(UUID hubId, Throwable t) {
        log.error("Hub Feign Client 호출 실패 (Fallback 처리): {}", t.getMessage());
        return HubResponseDto.builder()
                .hubId(hubId)
                .hubStatus(false)
                .build();
    }

}
