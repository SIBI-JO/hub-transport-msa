package com.sibijo.order.domain.enums;

import com.sibijo.common.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderDomainExceptionCode implements ExceptionCode {

    PRODUCT_NOT_EXIST(HttpStatus.NOT_FOUND, "상품 조회에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
