package com.sibijo.hub.exception.domain;

import com.sibijo.common.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum HubDomainExceptionCode implements ExceptionCode {

    HUB_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "유효하지 않은 허브 타입 입니다."),
    HUB_IS_DUPLICATED(HttpStatus.BAD_REQUEST, "이미 존재하는 허브 입니다."),
    HUB_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 허브 입니다."),
    INVALID_HUB_NAME(HttpStatus.BAD_REQUEST, "유효하지 않은 허브 이름 입니다."),
    INVALID_HUB_LOCATION(HttpStatus.BAD_REQUEST, "유효하지 않은 허브 주소 입니다."),
    ENCODING_ERROR(HttpStatus.BAD_REQUEST, "주소 인코딩 에러 입니다."),
    API_RESPONSE_ERROR(HttpStatus.BAD_REQUEST, "응답 API 에러 입니다."),
    LOCATION_NOT_FOUND(HttpStatus.BAD_REQUEST, "없는 주소 입니다."),
    INVALID_COORDINATE_FORMAT(HttpStatus.BAD_REQUEST, "좌표 변환 에러 입니다."),
    INVALID_API_RESPONSE(HttpStatus.BAD_REQUEST, "유효하지 않은 API 응답입니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "유효하지 않은 요청입니다."),
    API_SERVER_ERROR(HttpStatus.BAD_REQUEST, "서버 에러 입니다.");




    private final HttpStatus httpStatus;
    private final String message;
}
