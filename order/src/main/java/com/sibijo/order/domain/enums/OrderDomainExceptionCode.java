package com.sibijo.order.domain.enums;

import com.sibijo.common.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderDomainExceptionCode implements ExceptionCode {

    ;

    private final HttpStatus httpStatus;
    private final String message;
}
