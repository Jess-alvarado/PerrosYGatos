package com.behavior.pyg_behavior_case.dto.responses;

import com.behavior.pyg_behavior_case.models.PetType;

public record CasePetSnapshotResponse(
        Long id,
        Long originalPetId,
        String name,
        PetType type,
        String breed,
        Integer age,
        Boolean sterilized,
        String sex,
        String personalityDescription
){}