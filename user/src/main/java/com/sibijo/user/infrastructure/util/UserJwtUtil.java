package com.sibijo.user.infrastructure.util;

import com.sibijo.user.domain.enums.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserJwtUtil {

    // Header KEY 값
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // 사용자 권한 값의 KEY
    public static final String AUTHORIZATION_KEY = "auth";
    // 사용자 Hub ID
    public static final String HUB_ID = "hubId";
    // 사용자 Hub ID
    public static final String COMPANY_ID = "companyId";
    // Token 식별자
    public static final String BEARER_PREFIX = "Bearer ";
    // 토큰 만료시간
    private final long TOKEN_TIME = 60 * 60 * 1000L; // 60분

    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    // 로그 설정
    public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // 토큰 생성
    public String createToken(String username, Role role, String hubId, String CompanyId) {
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(username) // 사용자 식별자값(ID)
                        .claim(AUTHORIZATION_KEY, role) // 사용자 권한
                        .claim(HUB_ID, hubId) //hub id
                        .claim(COMPANY_ID, CompanyId) //company id
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME)) // 만료 시간
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                        .compact();
    }

    // 공통되는 것들(gateway와 중복)
    // 헤더에서 JWT 추출
//    public String extractToken(HttpServletRequest request) {
//        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            return authHeader.substring(7); // "Bearer " 제거 후 토큰 반환
//        }
//        return null;
//    }
//
//    // JWT에서 사용자 ID 추출
//    public Long extractUserId(String token) {
//        Claims claims = parseToken(token);
//        return Long.valueOf(claims.getSubject()); // JWT payload에서 userId 추출
//    }
//
//    // JWT에서 역할(Role) 추출
//    public String extractRole(String token) {
//        Claims claims = parseToken(token);
//        return claims.get("auth", String.class); // JWT payload에서 role 추출
//    }
//
//    //JWT에서 허브 ID 추출
//    public UUID extractHubId(String token) {
//        Claims claims = parseToken(token);
//        return claims.get("hubId", UUID.class); // JWT payload에서 role 추출
//    }
//
//    //JWT에서 허브 ID 추출
//    public UUID extractCompanyId(String token) {
//        Claims claims = parseToken(token);
//        return claims.get("companyId", UUID.class); // JWT payload에서 role 추출
//    }
//
//    // 토큰 파싱
//    private Claims parseToken(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(key)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
}
