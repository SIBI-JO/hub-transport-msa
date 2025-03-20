package com.sibijo.user.presentation.controller;

import com.sibijo.common.dto.ApiResponse;
import com.sibijo.user.application.service.UserService;
import com.sibijo.user.presentation.dto.user.SignUpRequestDto;
import com.sibijo.user.presentation.dto.user.SignUpResponseDto;
import com.sibijo.user.presentation.dto.user.UserCreateRequestDto;
import com.sibijo.user.presentation.dto.user.UserCreateResponseDto;
import com.sibijo.user.presentation.dto.user.UserDeleteResponseDto;
import com.sibijo.user.presentation.dto.user.UserDetailsResponseDto;
import com.sibijo.user.presentation.dto.user.UserPageResponseDto;
import com.sibijo.user.presentation.dto.user.UserUpdateRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/health-check")
    private ResponseEntity<String> healthCheck() {
        userService.TestFeignClient();
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
            @RequestBody UserCreateRequestDto requestDto,
            HttpServletRequest request) {

        UserCreateResponseDto createUserResponseDto = userService.createUser(requestDto, request);

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

    @PatchMapping("/{id}")
    private ResponseEntity<ApiResponse<UserDetailsResponseDto>> updateUser(
            @PathVariable("id") Long id,
            @Valid @RequestBody UserUpdateRequestDto requestDto,
            BindingResult bindingResult,
            HttpServletRequest request
    ) {
        // validation 예외처리
        raiseValidationException(bindingResult);

        UserDetailsResponseDto userDetailsResponseDto = userService.updateUser(id, requestDto,
                request);
        return ResponseEntity
                .ok(ApiResponse.success("수정 성공", userDetailsResponseDto));
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<ApiResponse<UserDeleteResponseDto>> deleteUser(
            @PathVariable("id") Long id,
            HttpServletRequest request
    ) {
        UserDeleteResponseDto userDeleteResponseDto = userService.deleteUser(id, request);
        return ResponseEntity
                .ok(ApiResponse.success("삭제 성공", userDeleteResponseDto));
    }

    @GetMapping("")
    private ResponseEntity<ApiResponse<UserPageResponseDto>> searchUsers(
            HttpServletRequest request,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size, //기본값 10
            @RequestParam(value = "orderby", defaultValue = "createdAt") String criteria,
            @RequestParam(value = "sort", defaultValue = "DESC") String sort,
            @RequestParam(value = "username", required = false) String username
    ) {
        // client 에서 1페이지 요청하면 0페이지를 반환하기 위해 page-1로 설정.
        UserPageResponseDto userPageResponseDto = userService.searchUsers(request, page,
                size, criteria, sort, username);
        return ResponseEntity
                .ok(ApiResponse.success("검색 성공", userPageResponseDto));
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
