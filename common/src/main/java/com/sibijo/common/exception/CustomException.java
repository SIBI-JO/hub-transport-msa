package com.sibijo.common.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{

    private final ExceptionCode exception;

    public CustomException(ExceptionCode exception) {
        super(exception.getMessage());
        this.exception = exception;
    }
}
