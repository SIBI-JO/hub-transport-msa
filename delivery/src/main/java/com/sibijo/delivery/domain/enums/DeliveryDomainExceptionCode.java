package com.sibijo.delivery.domain.enums;

import com.sibijo.common.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DeliveryDomainExceptionCode implements ExceptionCode {

    DELIVERY_NOT_EXIST(HttpStatus.NOT_FOUND, "존재하지 않는 배송입니다."),
    INVALID_DELIVERY_STATUS(HttpStatus.BAD_REQUEST, "유효하지 않은 배송 상태 값입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
