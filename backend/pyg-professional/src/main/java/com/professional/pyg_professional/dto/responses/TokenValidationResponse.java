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
@Schema(description = "Respuesta de validación de token desde pyg-auth")
public class TokenValidationResponse {

    @Schema(description = "ID del usuario en pyg-auth", example = "123")
    private Long userId;

    @Schema(description = "Nombre de usuario", example = "jess.alvarado")
    private String username;

    @Schema(description = "Rol del usuario", example = "ROLE_PROFESSIONAL")
    private String role;

    @Schema(description = "Token válido o no", example = "true")
    private boolean valid;

    @Schema(description = "Tiempo de expiración en timestamp", example = "1698987654000")
    private Long expiresAt;
}