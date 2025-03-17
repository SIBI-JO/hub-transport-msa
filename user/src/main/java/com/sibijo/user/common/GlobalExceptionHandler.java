package com.sibijo.user.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * 전역 에러 처리 클래스
 *
 *  CustomException : 커스텀 예외 발생
 *
 *  IllegalArgumentException : 잘못된 인자값이 전달된 경우
 *
 *  Exception : 예상치 못한 예외
 *
 *  MethodArgumentNotValidException : 유효성 검사 실패 시
 *  - dto 등 @Valid 검사 실패
 */

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<?>> handleCustomException(CustomException e) {
        log.error("CustomException : ", e);
        return handleExceptionInternal(e.getException());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(
            IllegalArgumentException e) {
        log.warn("IllegalArgumentException : ", e);
        return handleExceptionInternal(CommonExceptionCode.INVALID_PARAMETER);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        log.error("Exception : ", e);
        return handleExceptionInternal(CommonExceptionCode.INTERNAL_SERVER_ERROR);
    }

    //추후 확장
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders httpHeaders,
            HttpStatusCode httpStatusCode,
            WebRequest request
    ) {
        log.info("MethodArgumentNotValidException : ", ex);

        //첫번째 유효성 검증 오류 메세지 가져오기
        String errorMessage = ex.getBindingResult().getFieldError().getDefaultMessage();

        return ResponseEntity.badRequest()
                .body(ApiResponse.exception(errorMessage, CommonExceptionCode.VALIDATION_FAILED));
    }

    private ResponseEntity<ApiResponse<?>> handleExceptionInternal(ExceptionCode exceptionCode) {
        return ResponseEntity.status(exceptionCode.getHttpStatus())
                .body(ApiResponse.exception(exceptionCode.getMessage(), exceptionCode));
    }
}
