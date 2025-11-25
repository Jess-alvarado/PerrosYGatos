package com.professional.pyg_professional.clients;

import com.professional.pyg_professional.dto.responses.TokenValidationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Fallback implementation for AuthServiceClient
 * Executed when there are communication issues with pyg-auth
 */
@Component
public class AuthServiceClientFallback implements AuthServiceClient {
    private static final Logger log = LoggerFactory.getLogger(AuthServiceClientFallback.class);

    @Override
    public TokenValidationResponse validateToken(String token) {
        log.error("Error communicating with pyg-auth. Circuit breaker triggered.");
        throw new RuntimeException("Authentication service unavailable");
    }
}