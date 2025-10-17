package com.auth.pyg_auth.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth.pyg_auth.models.AuthResponse;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import com.auth.pyg_auth.models.LoginRequest;
import com.auth.pyg_auth.services.AuthService;
import com.auth.pyg_auth.models.UserRegisterRequest;
import com.auth.pyg_auth.models.ProfessionalRegisterRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping(value = "/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping(value = "/register/owner")
    public ResponseEntity<AuthResponse> registerOwner(@RequestBody UserRegisterRequest request) {
        return ResponseEntity.ok(authService.ownerRegister(request));
    }

    @PostMapping(value = "/register/professional")
    public ResponseEntity<AuthResponse> registerProfessional(@RequestBody ProfessionalRegisterRequest request) {
        return ResponseEntity.ok(authService.professionalRegister(request));
    }
}