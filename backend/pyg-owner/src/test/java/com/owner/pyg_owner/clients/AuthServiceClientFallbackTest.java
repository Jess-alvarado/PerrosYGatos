package com.owner.pyg_owner.clients;

import com.owner.pyg_owner.dto.responses.TokenValidationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthServiceClientFallbackTest {

    private final AuthServiceClientFallback fallback = new AuthServiceClientFallback();

    @Test
    @DisplayName("Fallback throws RuntimeException when auth service is unavailable")
    void validateToken_whenCircuitBreakerTriggered_shouldThrowRuntimeException() {
        assertThatThrownBy(() ->
                fallback.validateToken("Bearer any-token"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Authentication service unavailable");
    }

    @Test
    @DisplayName("Fallback throws regardless of token value")
    void validateToken_withAnyToken_shouldAlwaysThrow() {
        assertThatThrownBy(() -> fallback.validateToken(null))
                .isInstanceOf(RuntimeException.class);

        assertThatThrownBy(() -> fallback.validateToken(""))
                .isInstanceOf(RuntimeException.class);
    }
}