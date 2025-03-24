package com.sibijo.user.infrastructure.util;

import com.sibijo.user.domain.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Getter
public class UserJwtUtil {

    // 사용자 권한 값의 KEY
    public static final String AUTHORIZATION_KEY = "auth";
    // 사용자 ID
    public static final String USER_ID = "userId";
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
    public String createToken(String username, Long userId, Role role, UUID hubId, UUID CompanyId) {
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(username) // 사용자 식별자값(ID)
                        .claim(USER_ID, userId)
                        .claim(AUTHORIZATION_KEY, role) // 사용자 권한
                        .claim(HUB_ID, hubId) //hub id
                        .claim(COMPANY_ID, CompanyId) //company id
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME)) // 만료 시간
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                        .compact();
    }

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
