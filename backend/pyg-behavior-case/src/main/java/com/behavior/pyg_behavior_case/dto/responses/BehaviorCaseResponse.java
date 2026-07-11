package com.behavior.pyg_behavior_case.dto.responses;

import com.behavior.pyg_behavior_case.models.CaseStatus;

import java.time.LocalDateTime;
import java.util.List;

public record BehaviorCaseResponse(
        Long id,
        Long ownerId,
        String title,
        String description,
        String detailedDescription,
        Boolean hasChildren,
        Boolean hasOtherPets,
        Boolean hasAggression,
        Boolean isAloneFrequently,
        String behaviorDuration,
        CaseStatus status,
        List<CasePetSnapshotResponse> pets,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){}