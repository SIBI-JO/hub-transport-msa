package com.sibijo.gateway.infrastructure.filter;

import com.sibijo.gateway.infrastructure.util.GatewayJwtUtil;
import com.sibijo.gateway.infrastructure.util.GatewayUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter implements GlobalFilter, Ordered {

    private final GatewayJwtUtil jwtUtil;
    private final GatewayUtil gatewayUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // token 필요 없는 요청 처리
        if (gatewayUtil.isExemtedPath(path)) {
            log.info("JWT Authorization Filter: {}", path);
            return chain.filter(exchange);  // /signin/sign-up 경로는 필터를 적용하지 않음
        }

        String token = jwtUtil.extractToken(exchange);

        // token 유효성 체크
        if (token == null || !jwtUtil.validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }


    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}