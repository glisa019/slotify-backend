package com.myslotify.slotify.controller;

import com.myslotify.slotify.dto.AuthResponse;
import com.myslotify.slotify.dto.LoginRequest;
import com.myslotify.slotify.dto.ResetPasswordRequest;
import com.myslotify.slotify.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        logger.info("Login request for {}", request.getEmail());
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<AuthResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        logger.info("Reset password for {}", request.getEmail());
        return ResponseEntity.ok(authService.resetPassword(request));
    }

    @PostMapping("/admin/reset-password")
    public ResponseEntity<AuthResponse> resetAdminPassword(@RequestBody ResetPasswordRequest request) {
        logger.info("Reset admin password for {}", request.getEmail());
        return ResponseEntity.ok(authService.resetAdminPassword(request));
    }
}
