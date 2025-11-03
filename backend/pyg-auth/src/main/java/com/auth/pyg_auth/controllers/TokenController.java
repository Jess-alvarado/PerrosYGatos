package com.auth.pyg_auth.controllers;

import com.auth.pyg_auth.dto.responses.TokenValidationResponse;
import com.auth.pyg_auth.services.JwtService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class TokenController {

    private final JwtService jwtService;
    private static final Logger log = LoggerFactory.getLogger(TokenController.class);

    @Operation(
        summary = "Validar token JWT",
        description = "Valida un token JWT y retorna información del usuario"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Token validado exitosamente",
            content = @Content(schema = @Schema(implementation = TokenValidationResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Token inválido o expirado"
        )
    })
    @PostMapping(value = "/validate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenValidationResponse> validateToken(
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        log.info("Validation request received");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Authorization header missing or malformed: {}", authHeader);
            return ResponseEntity.status(401).body(
                TokenValidationResponse.builder()
                    .valid(false)
                    .build()
            );
        }

        try {
            log.info("Validating token from header: {}", authHeader);

            // 1. Clean token (remove "Bearer ")
            String token = authHeader.substring(7);
            log.info("Token extracted: {}", token);

            // 2. Extract claims
            Claims claims = jwtService.getAllClaims(token);
            log.debug("Extracted claims: {}", claims);

            // 3. Build response
            TokenValidationResponse resp = TokenValidationResponse.builder()
                    .valid(true)
                    .userId(claims.get("uid", Long.class))
                    .username(claims.getSubject())
                    .role(claims.get("role", String.class))
                    .expiresAt(claims.getExpiration().getTime())
                    .build();

            log.debug("Validation response: {}", resp);
            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            log.warn("Failed validating token: {}", e.getMessage());
            return ResponseEntity.status(401).body(
                TokenValidationResponse.builder()
                    .valid(false)
                    .build()
            );
        }
    }
}