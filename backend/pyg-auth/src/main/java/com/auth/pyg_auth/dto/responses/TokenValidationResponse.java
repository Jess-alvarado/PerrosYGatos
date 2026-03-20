package com.auth.pyg_auth.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Token validation response")
public class TokenValidationResponse {

    @Schema(description = "User ID", example = "123")
    private Long userId;

    @Schema(description = "Username", example = "jess.alvarado")
    private String username;

    @Schema(description = "User role", example = "ROLE_OWNER")
    private String role;

    @Schema(description = "Whether the token is valid", example = "true")
    private boolean valid;

    @Schema(description = "Expiration timestamp", example = "1698987654000")
    private Long expiresAt;
}