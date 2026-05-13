package com.owner.pyg_owner.controllers;

import com.owner.pyg_owner.dto.requests.OwnerCreateRequest;
import com.owner.pyg_owner.dto.responses.OwnerResponse;
import com.owner.pyg_owner.services.OwnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/owners")
@RequiredArgsConstructor
@Tag(name = "Owners", description = "API for managing owner profiles")
@SecurityRequirement(name = "bearerAuth")
public class OwnerController {

        private final OwnerService ownerService;

        @Operation(summary = "Create or update owner profile", description = "Creates a new owner profile or updates the existing one based on the authenticated user")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Owner profile created or updated successfully", content = @Content(schema = @Schema(implementation = OwnerResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid request data"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or expired JWT token")
        })
        @PostMapping("/profile")
        public ResponseEntity<OwnerResponse> upsertProfile(
                        @Parameter(hidden = true) @RequestHeader("Authorization") String authorization,
                        @Valid @RequestBody OwnerCreateRequest request) {
                return ResponseEntity.ok(ownerService.createOrUpdateProfile(authorization, request));
        }

        @Operation(summary = "Get current owner profile", description = "Returns the owner profile associated with the authenticated user")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Owner profile retrieved successfully", content = @Content(schema = @Schema(implementation = OwnerResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or expired JWT token"),
                        @ApiResponse(responseCode = "404", description = "Owner profile not found")
        })
        @GetMapping("/profile")
        public ResponseEntity<OwnerResponse> getMyProfile(
                        @Parameter(hidden = true) @RequestHeader("Authorization") String authorization) {
                return ResponseEntity.ok(ownerService.getMyProfile(authorization));
        }
}