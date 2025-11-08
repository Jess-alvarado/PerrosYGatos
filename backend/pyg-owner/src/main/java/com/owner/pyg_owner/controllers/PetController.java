package com.owner.pyg_owner.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    /**
     * Create a new pet for the authenticated owner
     */
    @PostMapping
    public ResponseEntity<PetResponse> addPet(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody PetRequest request
    ) {
        PetResponse response = petService.addPet(authorization, request);
        return ResponseEntity.ok(response);
    }

    /**
     * List all pets of the authenticated owner
     */
    @GetMapping
    public ResponseEntity<?> getPetsByOwner(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorization
    ) {
        return ResponseEntity.ok(petService.getPetsByOwner(authorization));
    }
}