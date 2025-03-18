package com.sibijo.common.utils.Auth;

import java.util.Set;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Component
public class AuthUtil {
    @Builder
    public <E> void checkSelf(String userName, String user, String role, Set<E> permitRoles) {
        log.info("userId: {}, id: {}, role: {}, permitRoles: {}", userName, user, role, permitRoles);
        if (permitRoles.contains(role)) {
            if (!userName.equals(user)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 사용자의 정보를 조회할 권한이 없습니다.");
            }
        }
    }

}
