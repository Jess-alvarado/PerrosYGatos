package com.behavior.pyg_behavior_case.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CaseProposalRequest(

        @NotBlank(message = "Approach is required")
        String approach,

        @NotNull(message = "Estimated price is required")
        @Positive(message = "Price must be positive")
        Integer estimatedPrice,

        @Positive(message = "Sessions must be positive")
        Integer estimatedSessions
){}