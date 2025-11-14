package com.owner.pyg_owner.controllers;

import com.owner.pyg_owner.services.OwnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.owner.pyg_owner.dto.requests.OwnerCreateRequest;
import com.owner.pyg_owner.dto.responses.OwnerResponse;

@RestController
@RequestMapping("/owners")
@RequiredArgsConstructor
@Tag(name = "Dueños de Mascotas", description = "API para gestionar perfiles de dueños de mascotas")
@SecurityRequirement(name = "bearerAuth")
public class OwnerController {

    private final OwnerService ownerService;

    @Operation(
        summary = "Crear o actualizar perfil de dueño",
        description = "Crea un nuevo perfil de dueño o actualiza uno existente basado en el token JWT"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Perfil creado/actualizado exitosamente",
            content = @Content(schema = @Schema(implementation = OwnerResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos de entrada inválidos"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autorizado - Token JWT inválido o expirado"
        )
    })

    @PostMapping
    public ResponseEntity<OwnerResponse> upsertProfile(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody OwnerCreateRequest request
    ) {
        return ResponseEntity.ok(ownerService.createOrUpdateProfile(authorization, request));
    }

    @Operation(
        summary = "Obtener perfil propio",
        description = "Obtiene el perfil del dueño autenticado basado en el token JWT"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Perfil recuperado exitosamente",
            content = @Content(schema = @Schema(implementation = OwnerResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autorizado - Token JWT inválido o expirado"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Perfil no encontrado"
        )
    })

    @GetMapping("/profile")
    public ResponseEntity<OwnerResponse> getMyProfile(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorization
    ) {
        return ResponseEntity.ok(ownerService.getMyProfile(authorization));
    }
}
