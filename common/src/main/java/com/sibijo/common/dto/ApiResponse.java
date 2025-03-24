package com.sibijo.common.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
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
