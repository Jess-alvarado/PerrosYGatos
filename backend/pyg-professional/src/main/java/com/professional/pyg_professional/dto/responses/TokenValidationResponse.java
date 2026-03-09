package com.professional.pyg_professional.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Token validation response from pyg-auth")
public class TokenValidationResponse {

    @Schema(description = "ID user pyg-auth", example = "123")
    private Long userId;

    @Schema(description = "user name", example = "jess.alvarado")
    private String username;

    @Schema(description = "Rol", example = "ROLE_PROFESSIONAL")
    private String role;

    @Schema(description = "Valid token or not", example = "true")
    private boolean valid;

    @Schema(description = "Expiration time in timestamp", example = "1698987654000")
    private Long expiresAt;
}