package com.sibijo.common.utils.Auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Base64;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtUtil {

    // Header KEY 값
    public static final String AUTHORIZATION_HEADER = "Authorization";

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // "Bearer " 제거 후 토큰 반환
        }
        return null;
    }

    // JWT에서 사용자 추출
    public String extractUserName(String token) {
        log.info("extract User Name");
        Claims claims = parseToken(token);
        return claims.getSubject(); // JWT payload에서 user 추출
    }

    // JWT에서 사용자 추출
    public Long extractUserID(String token) {
        log.info("extract User Id");
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class); // JWT payload에서 user 추출
    }

    // JWT에서 역할(Role) 추출
    public String extractRole(String token) {
        log.info("extractRole");
        Claims claims = parseToken(token);
        return claims.get("auth", String.class); // JWT payload에서 role 추출
    }

    //JWT에서 허브 ID 추출
    public UUID extractHubId(String token) {
        Claims claims = parseToken(token);
        return claims.get("hubId", UUID.class); // JWT payload에서 role 추출
    }

    //JWT에서 허브 ID 추출
    public UUID extractCompanyId(String token) {
        Claims claims = parseToken(token);
        return claims.get("companyId", UUID.class); // JWT payload에서 role 추출
    }

    // 토큰 파싱
    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
