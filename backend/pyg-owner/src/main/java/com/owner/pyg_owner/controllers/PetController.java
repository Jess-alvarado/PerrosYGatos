package com.owner.pyg_owner.controllers;

import com.owner.pyg_owner.dto.requests.PetRequest;
import com.owner.pyg_owner.dto.responses.PetResponse;
import com.owner.pyg_owner.services.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pets")
@Tag(name = "Pets", description = "API for managing pets linked to owner profiles")
@SecurityRequirement(name = "bearerAuth")
public class PetController {

        private final PetService petService;

        public PetController(PetService petService) {
                this.petService = petService;
        }

        @Operation(summary = "Create a new pet", description = "Creates a new pet linked to the authenticated user's owner profile. The owner profile must exist first.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Pet created successfully", content = @Content(schema = @Schema(implementation = PetResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid request data"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or expired JWT token"),
                        @ApiResponse(responseCode = "404", description = "Owner profile not found")
        })
        @PostMapping
        public ResponseEntity<PetResponse> addPet(
                        @Parameter(hidden = true) @RequestHeader("Authorization") String authorization,
                        @Valid @RequestBody PetRequest request) {
                PetResponse response = petService.addPet(authorization, request);
                return ResponseEntity.ok(response);
        }

        @Operation(summary = "List current user's pets", description = "Returns all pets linked to the authenticated user's owner profile")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Pets retrieved successfully", content = @Content(array = @ArraySchema(schema = @Schema(implementation = PetResponse.class)))),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or expired JWT token"),
                        @ApiResponse(responseCode = "404", description = "Owner profile not found")
        })
        @GetMapping
        public ResponseEntity<List<PetResponse>> getPetsByOwner(
                        @Parameter(hidden = true) @RequestHeader("Authorization") String authorization) {
                return ResponseEntity.ok(petService.getPetsByOwner(authorization));
        }

        @Operation(summary = "Get pet by id", description = "Returns a specific pet only if it belongs to the authenticated user")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Pet retrieved successfully", content = @Content(schema = @Schema(implementation = PetResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or expired JWT token"),
                        @ApiResponse(responseCode = "404", description = "Pet not found or does not belong to the authenticated user")
        })
        @GetMapping("/{petId}")
        public ResponseEntity<PetResponse> getPetById(
                        @Parameter(hidden = true) @RequestHeader("Authorization") String authorization,
                        @Parameter(description = "Pet ID", example = "1", required = true) @PathVariable Long petId) {
                PetResponse response = petService.getPetById(authorization, petId);
                return ResponseEntity.ok(response);
        }
}