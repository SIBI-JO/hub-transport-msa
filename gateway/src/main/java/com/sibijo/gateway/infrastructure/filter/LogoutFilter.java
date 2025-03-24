package com.sibijo.gateway.infrastructure.filter;

import com.sibijo.gateway.infrastructure.util.GatewayJwtUtil;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
@Slf4j
public class LogoutFilter extends AbstractGatewayFilterFactory<Object> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final GatewayJwtUtil jwtUtil;

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            if (exchange.getRequest().getPath().toString().equals("/api/logout")) {
                // 토큰 추출 & Redis 저장
                String token = jwtUtil.extractToken(exchange);
                try {
                    Date expiration = jwtUtil.extractExpiration(token);
                    long now = System.currentTimeMillis();
                    long ttl = expiration.getTime() - now;

                    if (ttl > 0) {
                        redisTemplate.opsForValue().set(
                                "blacklist:" + token,
                                "logout",
                                ttl,
                                TimeUnit.MILLISECONDS
                        );
                        log.info("Token blacklisted successfully");
                    }

                    exchange.getResponse().setStatusCode(HttpStatus.OK);
                    exchange.getResponse().getHeaders().add("Content-Type", "application/json");

                    String responseBody = "{\"message\": \"로그아웃 성공\"}";
                    DataBuffer buffer = exchange.getResponse().bufferFactory()
                            .wrap(responseBody.getBytes(StandardCharsets.UTF_8));

                    return exchange.getResponse().writeWith(Mono.just(buffer));

                } catch (Exception e) {
                    log.error("Invalid JWT token", e);
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }
            }
            return chain.filter(exchange);
        };
    }
}
