package com.sibijo.user.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

//    private final UserService userService;

    @GetMapping("/health-check")
    private ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok().body("OK");
    }

}
