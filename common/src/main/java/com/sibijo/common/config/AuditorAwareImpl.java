package com.sibijo.common.config;

import com.sibijo.common.utils.Auth.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<Long> {

    private final JwtUtil jwtUtil;
    private final HttpServletRequest request;

    public AuditorAwareImpl(JwtUtil jwtUtil, HttpServletRequest request) {
        this.jwtUtil = jwtUtil;
        this.request = request;
    }

    @Override
    public Optional<Long> getCurrentAuditor() {
        String token = jwtUtil.extractToken(request);
        if (token != null) {
            Long userId = jwtUtil.extractUserID(token);
            if (userId != null) {
                return Optional.of(userId);
            }
        }
        return Optional.empty();
    }
}
