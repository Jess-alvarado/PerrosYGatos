package com.professional.pyg_professional.controllers;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.*;
import com.professional.pyg_professional.dto.requests.ProfessionalRequest;
import com.professional.pyg_professional.services.ProfessionalService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/professionals")
@RequiredArgsConstructor
public class ProfessionalController {
    private final ProfessionalService professionalService;

    @PostMapping
    public String createProfile(@Parameter(hidden = true) @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody ProfessionalRequest request) {
        return ResponseEntity.ok(professionalService.createProfile(authorization, request)).toString();
    }
}
