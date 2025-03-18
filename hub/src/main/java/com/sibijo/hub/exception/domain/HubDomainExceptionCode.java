package com.sibijo.hub.exception.domain;

import com.sibijo.common.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum HubDomainExceptionCode implements ExceptionCode {

    HUB_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "유효하지 않은 허브 타입 입니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
