package com.sibijo.user.infrastructure.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sibijo.common.dto.ApiResponse;
import com.sibijo.common.exception.codes.CommonExceptionCode;
import com.sibijo.user.presentation.dto.user.SignInRequestDto;
import com.sibijo.user.presentation.dto.user.SignInResponseDto;
import com.sibijo.user.domain.enums.Role;
import com.sibijo.user.infrastructure.security.UserDetailsImpl;
import com.sibijo.user.infrastructure.util.UserJwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final UserJwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    public JwtAuthenticationFilter(UserJwtUtil jwtUtil, RedisTemplate<String, Object> redisTemplate) {
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
        setFilterProcessesUrl("/api/users/signin");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException {
        log.info("로그인 시도");
        try {
            SignInRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(),
                    SignInRequestDto.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getUsername(),
                            requestDto.getPassword(),
                            null
                    )
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
            HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        log.info("로그인 성공 및 JWT 생성");
        String username = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
        Long userId = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getId();
        Role role = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getRole();
        UUID hubId = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getHubId();
        UUID companyId = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getCompanyId();

        String token = jwtUtil.createToken(username, userId, role, hubId, companyId);
        long ttl = jwtUtil.getTOKEN_TIME();

        //redis에 저장
        redisTemplate.opsForValue().set("jwt:user:" + userId, token, ttl, TimeUnit.MILLISECONDS);

        //Jwt Token을 JSON 응답으로 반환
        ApiResponse<SignInResponseDto> responseBody = ApiResponse.success(
                "success", SignInResponseDto.builder().token(token).build()
        );

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        new ObjectMapper().writeValue(response.getWriter(), responseBody);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
            HttpServletResponse response, AuthenticationException failed)
            throws IOException, ServletException {
        log.info("로그인 실패");

        ApiResponse<String> responseBody = ApiResponse.exception(
                CommonExceptionCode.UNAUTHORIZED_ACCESS.getMessage(),
                String.valueOf(CommonExceptionCode.UNAUTHORIZED_ACCESS.getHttpStatus()));

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        new ObjectMapper().writeValue(response.getWriter(), responseBody);
    }
}
