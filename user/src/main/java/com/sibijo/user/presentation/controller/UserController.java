package com.sibijo.user.presentation.controller;

import com.sibijo.user.application.service.UserService;
import com.sibijo.user.common.ApiResponse;
import com.sibijo.user.presentation.dto.SignUpRequestDto;
import com.sibijo.user.presentation.dto.SignUpResponseDto;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
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

        // validation global exception handler에서 자동 예외처리

        SignUpResponseDto signUpResponseDto = userService.signup(requestDto);

        return ResponseEntity
                .created(URI.create("/users/" + signUpResponseDto.getUserId()))
                .body(ApiResponse.success("성공", signUpResponseDto));
    }

}
