package com.professional.pyg_professional.controllers;

import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.professional.pyg_professional.dto.requests.ProfessionalRequest;
import com.professional.pyg_professional.dto.requests.ProfessionalUpdateRequest;
import com.professional.pyg_professional.dto.responses.ProfessionalResponse;
import com.professional.pyg_professional.services.ProfessionalService;

@RestController
@RequestMapping("/professionals")
@RequiredArgsConstructor
@Tag(name = "Professionals", description = "API for managing professional profiles")
@SecurityRequirement(name = "bearerAuth")
public class ProfessionalController {

        private final ProfessionalService professionalService;

        @Operation(summary = "Create professional profile", description = "Creates the profile for the authenticated user with the PROFESSIONAL role")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Professional profile created successfully", content = @Content(schema = @Schema(implementation = ProfessionalResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Invalid or expired token"),
                        @ApiResponse(responseCode = "403", description = "User is not allowed to create a professional profile"),
                        @ApiResponse(responseCode = "409", description = "Professional profile already exists")
        })
        @PostMapping("/profile")
        public ResponseEntity<ProfessionalResponse> createProfile(
                        @Parameter(hidden = true) @RequestHeader("Authorization") String authorization,
                        @Valid @RequestBody ProfessionalRequest request) {

                ProfessionalResponse response = professionalService.createProfile(authorization, request);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @Operation(summary = "Get my professional profile", description = "Returns the professional profile of the authenticated user with the PROFESSIONAL role")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Professional profile retrieved successfully", content = @Content(schema = @Schema(implementation = ProfessionalResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Invalid or expired token"),
                        @ApiResponse(responseCode = "403", description = "User is not allowed to view this profile"),
                        @ApiResponse(responseCode = "404", description = "Professional profile not found")
        })
        @GetMapping("/profile")
        public ResponseEntity<ProfessionalResponse> getMyProfile(
                        @Parameter(hidden = true) @RequestHeader("Authorization") String authorization) {
                return ResponseEntity.ok(professionalService.getMyProfile(authorization));
        }

        @Operation(summary = "Update my professional profile", description = "Partially updates the professional profile of the authenticated user with the PROFESSIONAL role")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Professional profile updated successfully", content = @Content(schema = @Schema(implementation = ProfessionalResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Invalid or expired token"),
                        @ApiResponse(responseCode = "403", description = "User is not allowed to update this profile"),
                        @ApiResponse(responseCode = "404", description = "Professional profile not found")
        })
        @PatchMapping("/profile")
        public ResponseEntity<ProfessionalResponse> updateMyProfile(
                        @Parameter(hidden = true) @RequestHeader("Authorization") String authorization,
                        @Valid @RequestBody ProfessionalUpdateRequest request) {
                return ResponseEntity.ok(professionalService.updateMyProfile(authorization, request));
        }

        @Operation(summary = "List all professional profiles", description = "Returns all professional profiles. Available for OWNER and PROFESSIONAL roles")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Professional profiles retrieved successfully"),
                        @ApiResponse(responseCode = "401", description = "Invalid or expired token"),
                        @ApiResponse(responseCode = "403", description = "User is not allowed to list professional profiles")
        })
        @GetMapping
        public ResponseEntity<List<ProfessionalResponse>> getAllProfessionals(
                        @Parameter(hidden = true) @RequestHeader("Authorization") String authorization) {
                return ResponseEntity.ok(professionalService.getAllProfessionals(authorization));
        }

        @Operation(summary = "Get professional profile by ID", description = "Returns a professional profile by its ID. Available for OWNER and PROFESSIONAL roles")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Professional profile retrieved successfully", content = @Content(schema = @Schema(implementation = ProfessionalResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Invalid or expired token"),
                        @ApiResponse(responseCode = "403", description = "User is not allowed to view this profile"),
                        @ApiResponse(responseCode = "404", description = "Professional profile not found")
        })
        @GetMapping("/{id}")
        public ResponseEntity<ProfessionalResponse> getProfessionalById(
                        @Parameter(hidden = true) @RequestHeader("Authorization") String authorization,
                        @PathVariable Long id) {
                return ResponseEntity.ok(professionalService.getProfessionalById(authorization, id));
        }
}