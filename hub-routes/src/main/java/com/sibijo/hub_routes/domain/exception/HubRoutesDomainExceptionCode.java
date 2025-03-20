package com.sibijo.hub_routes.domain.exception;

import com.sibijo.common.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum HubRoutesDomainExceptionCode implements ExceptionCode {

    INVALID_HUB_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 허브 경로 아이디 입니다."),
    HUB_ROUTES_IS_DUPLICATED(HttpStatus.BAD_REQUEST, "이미 존재하는 허브 경로 아이디 입니다."),
    HUB_ROUTES_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 허브 경로 입니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
