package com.sibijo.common.config;

import com.sibijo.common.utils.Auth.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditorAwareConfig {
    private final JwtUtil jwtUtil;
    private final HttpServletRequest request;

    public AuditorAwareConfig(JwtUtil jwtUtil, HttpServletRequest request) {
        this.jwtUtil = jwtUtil;
        this.request = request;
    }

    @Bean
    public AuditorAware<Long> auditorProvider() {
        return new AuditorAwareImpl(jwtUtil, request);
    }
}
