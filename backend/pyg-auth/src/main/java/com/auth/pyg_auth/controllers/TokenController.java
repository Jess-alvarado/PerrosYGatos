package com.auth.pyg_auth.controllers;

import com.auth.pyg_auth.dto.responses.TokenValidationResponse;
import com.auth.pyg_auth.services.AccessTokenBlacklistService;
import com.auth.pyg_auth.services.JwtService;
import io.jsonwebtoken.Claims;
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
    private final AccessTokenBlacklistService accessTokenBlacklistService;
    private static final Logger log = LoggerFactory.getLogger(TokenController.class);

    @PostMapping(value = "/validate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenValidationResponse> validateToken(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || authHeader.isBlank()) {
            return ResponseEntity.status(401).body(
                    TokenValidationResponse.builder().valid(false).build());
        }
        
        try {
            String token = authHeader.trim().replaceFirst("(?i)^Bearer\\s+", "").trim();

            String jti = jwtService.getJtiFromToken(token);

            if (accessTokenBlacklistService.isBlacklisted(jti)) {
                return ResponseEntity.status(401).body(
                        TokenValidationResponse.builder().valid(false).build());
            }

            Claims claims = jwtService.getAllClaims(token);

            TokenValidationResponse response = TokenValidationResponse.builder()
                    .valid(true)
                    .userId(claims.get("uid", Long.class))
                    .username(claims.getSubject())
                    .role(claims.get("role", String.class))
                    .expiresAt(claims.getExpiration().getTime())
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage(), e);
            return ResponseEntity.status(401).body(
                    TokenValidationResponse.builder().valid(false).build());
        }
    }
}