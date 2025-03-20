package com.sibijo.user.presentation.controller;

import com.sibijo.common.dto.ApiResponse;
import com.sibijo.user.application.service.AuthService;
import com.sibijo.user.presentation.dto.auth.CommonSignUpRequestDto;
import com.sibijo.user.presentation.dto.auth.MasterSignUpRequestDto;
import com.sibijo.user.presentation.dto.user.SignUpResponseDto;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup/master") // 마스터 회원가입
    private ResponseEntity<ApiResponse<SignUpResponseDto>> signUpMaster(
            @RequestBody @Valid MasterSignUpRequestDto requestDto, BindingResult bindingResult) {

        // TODO: validation => global exception handler에서 자동 예외처리?
        raiseValidationException(bindingResult);

        SignUpResponseDto signUpResponseDto = authService.signUpMaster(requestDto);

        return ResponseEntity
                .created(URI.create("/users" + signUpResponseDto.getUserId()))
                .body(ApiResponse.success("성공", signUpResponseDto));
    }

    @PostMapping("/signup/common") // 마스터 회원가입
    private ResponseEntity<ApiResponse<SignUpResponseDto>> signUpCommon(
            @RequestBody @Valid CommonSignUpRequestDto requestDto, BindingResult bindingResult) {

        // TODO: validation => global exception handler에서 자동 예외처리?
        raiseValidationException(bindingResult);

        SignUpResponseDto signUpResponseDto = authService.signUpCommon(requestDto);

        return ResponseEntity
                .created(URI.create("/users" + signUpResponseDto.getUserId()))
                .body(ApiResponse.success("성공", signUpResponseDto));
    }

    // Master 유저 Role 부여


    private static void raiseValidationException(BindingResult bindingResult) {
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if (fieldErrors.size() > 0) {
            for (FieldError fieldError : fieldErrors) {
                throw new IllegalArgumentException(fieldError.getDefaultMessage());
            }
        }
    }


}
