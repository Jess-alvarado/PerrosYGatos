package com.auth.pyg_auth.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth.pyg_auth.models.AuthResponse;
import com.auth.pyg_auth.models.MessageResponse;
import com.auth.pyg_auth.models.TokenResponse;

import lombok.RequiredArgsConstructor;
import com.auth.pyg_auth.models.LoginRequest;
import com.auth.pyg_auth.models.UserRegisterRequest;
import com.auth.pyg_auth.services.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping(value = "/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping(value = "/register")
    public ResponseEntity<AuthResponse> register(@RequestBody UserRegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    /*
    @PostMapping("/refresh")
    public TokenResponse refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader) {
        return authService.refreshToken(authHeader);
    }
    */

    @PostMapping(value = "/logout")
    public ResponseEntity<MessageResponse> logout(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest()
                    .body(MessageResponse.builder().message("Authorization header missing or invalid").build());
        }
        String token = authHeader.substring(7);
        authService.logout(token);
        return ResponseEntity.ok(MessageResponse.builder().message("Logout successful").build());
    }
}