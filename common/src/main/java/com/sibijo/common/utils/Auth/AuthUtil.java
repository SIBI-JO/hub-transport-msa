package com.sibijo.common.utils.Auth;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Set;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Component
public class AuthUtil {

    private final JwtUtil jwtUtil;

    public AuthUtil(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // 사용자 본인인지
    public <E> void authorizeSelfAccess(HttpServletRequest request, Long id, Set<E> targetRoles) {

        //JWT parsing
        String token = jwtUtil.extractToken(request);
        String role = jwtUtil.extractRole(token);
        Long userId = jwtUtil.extractUserID(token);

        if (role == null || userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "토큰에서 사용자 정보를 추출할 수 없습니다.");
        }

        // Authorization Detail check
        log.info("userId: {}, id: {}, role: {}, permitRoles: {}", userId, id, role, targetRoles);
        if (targetRoles.contains(role)) {
            if (!userId.equals(id)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 사용자의 정보를 조회할 권한이 없습니다.");
            }
        }
    }

    // 담당 허브인지
    public <E> void authoizeHubAccess(HttpServletRequest request, UUID hubId, Set<E> targetRoles) {
        //JWT parsing
        String token = jwtUtil.extractToken(request);
        String role = jwtUtil.extractRole(token);
        UUID tokenHubId = jwtUtil.extractHubId(token);

        if (role == null || tokenHubId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "토큰에서 사용자 정보를 추출할 수 없습니다.");
        }

        // Authorization Detail check
        log.info("userId: {}, id: {}, role: {}, permitRoles: {}", tokenHubId, hubId, role, targetRoles);
        if (targetRoles.contains(role)) {
            if (!tokenHubId.equals(hubId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 사용자의 정보를 조회할 권한이 없습니다.");
            }
        }
    }

    // 여기에 필요한 검증 메서드 추가해서 사용
    // 담당 배송인지

    // 본인 업체인지

}
