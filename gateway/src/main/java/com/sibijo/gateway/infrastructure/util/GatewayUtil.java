package com.sibijo.gateway.infrastructure.util;

import org.springframework.stereotype.Component;

@Component
public class GatewayUtil {

    public boolean isExemtedPath(String path) {
        return path.equals("/api/users/signin")
                || path.startsWith("/api/users/signup")
                || path.startsWith("/api/users/health-check")
                || path.startsWith("/swagger")
                || path.startsWith("/swagger-ui");
    }

}
