package com.behavior.pyg_behavior_case.dto.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record BehaviorCaseRequest(

        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "Description is required")
        String description,

        String detailedDescription,

        // Preguntas estructuradas
        Boolean hasChildren,
        Boolean hasOtherPets,
        Boolean hasAggression,
        Boolean isAloneFrequently,

        String behaviorDuration,

        // Mascotas involucradas — al menos una
        @NotEmpty(message = "At least one pet must be involved")
        @Valid
        List<CasePetSnapshot> pets
){}