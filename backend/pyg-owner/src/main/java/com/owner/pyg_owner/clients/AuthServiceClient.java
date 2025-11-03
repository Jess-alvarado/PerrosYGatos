package com.owner.pyg_owner.clients;

import com.owner.pyg_owner.dto.responses.AuthValidationResponse;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Feign client for communication with the pyg-auth service
 * Allows validating tokens and retrieving authenticated user information
 */
@FeignClient(
    name = "auth-service",
    url = "${auth.service.url:http://localhost:8081}",  // Default URL, configurable in application.yml
    fallback = AuthServiceClientFallback.class         // Error handling fallback when pyg-auth is unavailable
)
@Hidden  // Ocultar de Swagger UI
public interface AuthServiceClient {
    /**
     * Validates a JWT token and returns user information
     *
     * @param token Full JWT token (including the "Bearer " prefix)
     * @return User information if the token is valid
     */
    @PostMapping("/auth/validate")
    AuthValidationResponse validateToken(@RequestHeader("Authorization") String token);
}