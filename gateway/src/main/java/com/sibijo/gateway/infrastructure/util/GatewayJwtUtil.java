package com.sibijo.gateway.infrastructure.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Slf4j
@Component
public class GatewayJwtUtil {

    @Value("${service.jwt.secret-key}")
    private String secretKey;

    // util
    public String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        log.info("##### Raw Authorization Header: '{}'", authHeader);  // 원본 헤더
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7).trim();
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
                    .getBody();
            log.info("#####payload :: " + claims.toString());

            // 추가적인 검증 로직 (예: 토큰 만료 여부 확인 등)을 여기에 추가할 수 있습니다.
            return true;
        } catch (Exception e) {
            log.error("######error :: " + e.getMessage());
            return false;
        }
    }

    // 토큰에서 Role 추출하는 메서드
    public String extractRole(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
                    .getBody();

            return claims.get("auth", String.class);  // JWT의 'auth(role)' 클레임 추출
        } catch (Exception e) {
            log.error("Failed to extract role from token: {}", e.getMessage());
            return null;
        }
    }

    // 토큰에서 expiration 추출하는 메서드
    public Date extractExpiration(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
                    .getBody();

            return claims.getExpiration();  // JWT의 'auth(role)' 클레임 추출
        } catch (Exception e) {
            log.error("Failed to extract expiration from token: {}", e.getMessage());
            return null;
        }
    }


}
