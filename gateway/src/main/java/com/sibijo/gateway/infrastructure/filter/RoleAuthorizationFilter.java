package com.sibijo.gateway.infrastructure.filter;


import com.sibijo.gateway.infrastructure.util.GatewayJwtUtil;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RoleAuthorizationFilter extends AbstractGatewayFilterFactory<RoleAuthorizationFilter.Config> {

    private final GatewayJwtUtil jwtUtil;

    public RoleAuthorizationFilter(GatewayJwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            String method = exchange.getRequest().getMethod().toString();
            String token = jwtUtil.extractToken(exchange);

            // token 필요 없는 요청 처리
            if (path.equals("/api/users/signin") || path.startsWith("/api/users/signup") || path.startsWith(
                    "/api/users/health-check")) {
                log.info("JWT Authorization Filter: {}", path);
                return chain.filter(exchange);  // /signin/sign-up 경로는 필터를 적용하지 않음
            }

            // TODO: role 체크
            String role = jwtUtil.extractRole(token);
            log.info("Role Filter: role: {}", role);
            if (role == null) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            // 역할별 허용된 HTTP 메서드 체크
            Set<String> allowedMethods = config.getRolePermissions().getOrDefault(role, Set.of());
            log.info("Role Filter: allowedMethods, method: {}, {}",allowedMethods, method);
            if (!allowedMethods.contains(method)) {
                log.warn("Unauthorized Access Attempt: Role={} Path={} Method={}", role, path, method);
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            return chain.filter(exchange);
        };
    }

    @Getter
    @Setter
    public static class Config {
        private Map<String, Set<String>> rolePermissions;

        public Config(Map<String, Set<String>> rolePermissions) {
            this.rolePermissions = rolePermissions;
        }
    }
}
