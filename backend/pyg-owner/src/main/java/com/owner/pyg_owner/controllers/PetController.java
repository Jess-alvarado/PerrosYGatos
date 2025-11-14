package com.owner.pyg_owner.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.owner.pyg_owner.dto.requests.PetRequest;
import com.owner.pyg_owner.dto.responses.PetResponse;
import com.owner.pyg_owner.services.PetService;


@RestController
@RequestMapping("/pets")
@Tag(name = "Mascotas", description = "API para gestionar las mascotas de los dueños")
@SecurityRequirement(name = "bearerAuth")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @Operation(
        summary = "Registrar nueva mascota",
        description = "Registra una nueva mascota asociada al dueño autenticado. El dueño debe tener un perfil creado previamente."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Mascota registrada exitosamente",
            content = @Content(schema = @Schema(implementation = PetResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos de entrada inválidos (validaciones fallidas)"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autorizado - Token JWT inválido o expirado"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Perfil de dueño no encontrado - Debe crear su perfil primero"
        )
    })
    @PostMapping
    public ResponseEntity<PetResponse> addPet(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody PetRequest request
    ) {
        PetResponse response = petService.addPet(authorization, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Listar mascotas propias",
        description = "Obtiene la lista completa de mascotas registradas por el dueño autenticado"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de mascotas recuperada exitosamente (puede estar vacía)",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = PetResponse.class)))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autorizado - Token JWT inválido o expirado"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Perfil de dueño no encontrado"
        )
    })
    @GetMapping
    public ResponseEntity<List<PetResponse>> getPetsByOwner(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorization
    ) {
        return ResponseEntity.ok(petService.getPetsByOwner(authorization));
    }

    @Operation(
        summary = "Obtener mascota específica",
        description = "Obtiene los detalles de una mascota específica del dueño autenticado. Solo se puede acceder a mascotas propias."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Mascota encontrada y recuperada exitosamente",
            content = @Content(schema = @Schema(implementation = PetResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autorizado - Token JWT inválido o expirado"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Mascota no encontrada o no pertenece al usuario autenticado"
        )
    })
    @GetMapping("/{petId}")
    public ResponseEntity<PetResponse> getPetById(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorization,
            @Parameter(description = "ID de la mascota", example = "1", required = true) @PathVariable Long petId
    ) {
        PetResponse response = petService.getPetById(authorization, petId);
        return ResponseEntity.ok(response);
    }
}