package com.sibijo.gateway.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GateWayController {

    @GetMapping("/health-check")
    private ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok().body("OK, gateway health check");
    }
}

