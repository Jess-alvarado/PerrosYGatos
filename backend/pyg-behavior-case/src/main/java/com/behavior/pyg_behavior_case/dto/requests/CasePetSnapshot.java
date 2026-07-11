package com.behavior.pyg_behavior_case.dto.requests;

import com.behavior.pyg_behavior_case.models.PetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CasePetSnapshot(

        @NotNull(message = "Original pet ID is required")
        Long originalPetId,

        @NotBlank(message = "Pet name is required")
        String name,

        @NotNull(message = "Pet type is required")
        PetType type,

        String breed,
        Integer age,
        Boolean sterilized,
        String sex,
        String personalityDescription
){}