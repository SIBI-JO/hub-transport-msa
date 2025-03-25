package com.sibijo.hub_routes.domain.service;

import com.sibijo.common.exception.CustomException;
import com.sibijo.common.exception.codes.CommonExceptionCode;
import com.sibijo.hub_routes.presentation.dto.HubRoutesResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class HubRouteCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, String> stringCustomRedisTemplate;
    private static final String RECENT_ROUTES_KEY = "recent_routes";
    private static final int MAX_CACHE_SIZE = 10;

    //캐시 데이터 조회
    public HubRoutesResponseDto getCachedRecentRoutes(String hash) {
        try {
            HubRoutesResponseDto cachedRoute = (HubRoutesResponseDto) redisTemplate.opsForValue().get(hash);
            if (cachedRoute != null) {
                log.info("캐시에서 경로를 찾았습니다. 해시: {}", hash);
            } else {
                log.warn("캐시에서 경로를 찾을 수 없습니다. 해시: {}", hash);
                return null;
            }
            return cachedRoute;
        } catch (Exception e) {
            log.error("캐시 조회 중 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(CommonExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    // 캐시에 데이터 최신 갯수 저장
    public void createCacheRoute(String hash, HubRoutesResponseDto dto) {
        try {
            //Dto 저장, TTL 1시간
            redisTemplate.opsForValue().set(hash, dto, 1, TimeUnit.HOURS);

            // 최신 순으로 정렬된 Set에 추가
            stringCustomRedisTemplate.opsForZSet().add(RECENT_ROUTES_KEY, hash, System.currentTimeMillis());

            // 오래된 데이터 제거
            redisTemplate.opsForZSet().removeRange(RECENT_ROUTES_KEY, 0, -(MAX_CACHE_SIZE + 1)); //상위 10개 유지
        } catch (Exception e) {
            log.error("캐시 저장 중 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(CommonExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }
}
