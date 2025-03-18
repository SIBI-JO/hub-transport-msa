package com.sibijo.user.presentation.controller;

import com.sibijo.common.dto.ApiResponse;
import com.sibijo.user.application.service.UserService;
import com.sibijo.user.presentation.dto.SignUpRequestDto;
import com.sibijo.user.presentation.dto.SignUpResponseDto;
import com.sibijo.user.presentation.dto.UserCreateRequestDto;
import com.sibijo.user.presentation.dto.UserCreateResponseDto;
import com.sibijo.user.presentation.dto.UserDetailsResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/health-check")
    private ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok().body("OK");
    }

    @PostMapping("/signup") //회원가입
    private ResponseEntity<ApiResponse<SignUpResponseDto>> signUp(
            @RequestBody @Valid SignUpRequestDto requestDto, BindingResult bindingResult) {

        // TODO: validation => global exception handler에서 자동 예외처리?
        raiseValidationException(bindingResult);

        SignUpResponseDto signUpResponseDto = userService.signup(requestDto);

        return ResponseEntity
                .created(URI.create("/users" + signUpResponseDto.getUserId()))
                .body(ApiResponse.success("성공", signUpResponseDto));
    }

    //CRUDS
    @PostMapping("")
    private ResponseEntity<ApiResponse<UserCreateResponseDto>> createUser(
            @RequestBody UserCreateRequestDto requestDto) {

        UserCreateResponseDto createUserResponseDto = userService.createUser(requestDto);

        return ResponseEntity
                .created(URI.create("/users" + createUserResponseDto.getUserId()))
                .body(ApiResponse.success("성공", createUserResponseDto));
    }

    @GetMapping("/{id}")
    private ResponseEntity<ApiResponse<UserDetailsResponseDto>> getUser(
            @PathVariable("id") Long id,
            HttpServletRequest request
    ) {
        UserDetailsResponseDto userDetailsResponseDto = userService.getUser(id, request);
        return ResponseEntity
                .ok(ApiResponse.success("success", userDetailsResponseDto));
    }

    private static void raiseValidationException(BindingResult bindingResult) {
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if (fieldErrors.size() > 0) {
            for (FieldError fieldError : fieldErrors) {
                throw new IllegalArgumentException(fieldError.getDefaultMessage());
            }
        }
    }

}
