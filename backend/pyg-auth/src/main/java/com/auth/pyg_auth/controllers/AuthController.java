package com.auth.pyg_auth.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.auth.pyg_auth.dto.requests.LoginRequest;
import com.auth.pyg_auth.dto.requests.UserRegisterRequest;
import com.auth.pyg_auth.dto.responses.AuthResponse;
import com.auth.pyg_auth.services.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints para registro y login de usuarios en PerrosYGatos")
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Iniciar sesión", description = "Valida las credenciales y genera un token JWT.")
    @PostMapping(value = "/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Registrar nuevo usuario", description = "Crea un nuevo usuario general y devuelve su token JWT.")
    @PostMapping(value = "/register")
    public ResponseEntity<AuthResponse> register(@RequestBody UserRegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
}