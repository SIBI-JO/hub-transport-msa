package com.sibijo.user.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {

    private static final String success = "SUCCESS";
    private static final String fail = "FAIL";

    private String status;
    private String message;
    private T data;

    //성공
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(success, message, data);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(success, message, null);
    }

    //실패
    public static <T> ApiResponse<T> exception(String message, T errorCode) {
        return new ApiResponse<>(fail, message, errorCode);
    }
}
