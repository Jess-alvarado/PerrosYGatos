package com.professional.pyg_professional.controllers;

import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.professional.pyg_professional.dto.requests.ProfessionalRequest;
import com.professional.pyg_professional.dto.requests.ProfessionalUpdateRequest;
import com.professional.pyg_professional.dto.responses.ProfessionalResponse;
import com.professional.pyg_professional.services.ProfessionalService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/professionals")
@RequiredArgsConstructor
@Tag(name = "Professionals", description = "API for managing professional profiles")
@SecurityRequirement(name = "bearerAuth")
public class ProfessionalController {

        private final ProfessionalService professionalService;

        @Operation(summary = "Create professional profile", description = "Creates a new professional profile linked to the authenticated user")
        @ApiResponses({
                @ApiResponse(responseCode = "201", description = "Professional profile created successfully", content = @Content(schema = @Schema(implementation = ProfessionalResponse.class))),
                @ApiResponse(responseCode = "400", description = "Invalid request data"),
                @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or expired JWT token"),
                @ApiResponse(responseCode = "409", description = "Conflict - Profile already exists for this user")
        })
        @PostMapping("/profile")
        public ResponseEntity<ProfessionalResponse> createProfile(
                @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
                @Valid @RequestBody ProfessionalRequest request) {

                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(professionalService.createProfile(userId, request));
        }

        @Operation(summary = "Get current professional profile", description = "Returns the professional profile associated with the authenticated user")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Professional profile retrieved successfully", content = @Content(schema = @Schema(implementation = ProfessionalResponse.class))),
                @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or expired JWT token"),
                @ApiResponse(responseCode = "404", description = "Professional profile not found")
        })
        @GetMapping("/profile")
        public ResponseEntity<ProfessionalResponse> getMyProfile(
                @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId) {

                return ResponseEntity.ok(professionalService.getMyProfile(userId));
        }

        @Operation(summary = "Update current professional profile", description = "Partially updates the professional profile fields for the authenticated user")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Professional profile updated successfully", content = @Content(schema = @Schema(implementation = ProfessionalResponse.class))),
                @ApiResponse(responseCode = "400", description = "Invalid request data"),
                @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or expired JWT token"),
                @ApiResponse(responseCode = "404", description = "Professional profile not found")
        })
        @PatchMapping("/profile")
        public ResponseEntity<ProfessionalResponse> updateMyProfile(
                @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
                @Valid @RequestBody ProfessionalUpdateRequest request) {

                return ResponseEntity.ok(professionalService.updateMyProfile(userId, request));
        }

        @Operation(summary = "List all professional profiles", description = "Returns a list of all registered professional profiles in the system. Accessible by owners and professionals.")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "List of professionals retrieved successfully", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProfessionalResponse.class)))),
                @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or expired JWT token")
        })
        @GetMapping
        public ResponseEntity<List<ProfessionalResponse>> getAllProfessionals() {
                return ResponseEntity.ok(professionalService.getAllProfessionals());
        }

        @Operation(summary = "Get professional profile by ID", description = "Returns a specific professional profile based on its unique ID database record")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Professional profile retrieved successfully", content = @Content(schema = @Schema(implementation = ProfessionalResponse.class))),
                @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or expired JWT token"),
                @ApiResponse(responseCode = "404", description = "Professional profile not found")
        })
        @GetMapping("/{id}")
        public ResponseEntity<ProfessionalResponse> getProfessionalById(
                @Parameter(description = "Professional Profile ID", example = "1", required = true) @PathVariable Long id) {
                return ResponseEntity.ok(professionalService.getProfessionalById(id));
        }
}