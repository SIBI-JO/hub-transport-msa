package com.sibijo.hub_routes.infrastructure.client;

import com.sibijo.hub_routes.infrastructure.dto.HubServiceClientDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HubServiceClientFallback implements FallbackFactory<HubServiceClient> {

    /**
     * Fallback 메서드
     *
     */
    @Override
    public HubServiceClient create(Throwable cause) {
        return new HubServiceClient() {

            @Override
            public HubServiceClientDto getHubForHubRoutes() {
                log.error("허브 서비스가 응답하지 않습니다., 원인: {}", cause.getMessage());
                return new HubServiceClientDto();
            }
        };
    }
}
