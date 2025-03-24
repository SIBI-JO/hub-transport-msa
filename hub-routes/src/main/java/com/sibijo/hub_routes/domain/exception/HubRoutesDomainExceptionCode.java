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
    HUB_ROUTES_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 허브 경로 입니다."),
    INVALID_HUB_ROUTE_DISTANCE(HttpStatus.BAD_REQUEST, "유효하지 않은 허브 경로 거리값 입니다."),
    INVALID_HUB_ROUTE_TIME(HttpStatus.BAD_REQUEST, "유효하지 않은 허브 경로 시간값 입니다."),
    HUB_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 허브 입니다."),
    INVALID_HUB_NAME_FOR_HUB_ROUTES(HttpStatus.BAD_REQUEST, "유효하지 않은 허브 경로 생성을 위한 출발, 도착 허브 이름 입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
