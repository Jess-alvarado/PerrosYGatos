package com.owner.pyg_owner.clients;

import com.owner.pyg_owner.dto.responses.TokenValidationResponse;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
    name = "pyg-auth",
    url = "${AUTH_SERVICE_URL:http://localhost:8081}",
    fallback = AuthServiceClientFallback.class
)
@Hidden
public interface AuthServiceClient {
    /**
     * Validates a JWT token and returns user information
     *
     * @param token Full JWT token (including the "Bearer " prefix)
     * @return User information if the token is valid
     */
    @PostMapping("/api/auth/validate")
    TokenValidationResponse validateToken(@RequestHeader("Authorization") String token);
}