package com.behavior.pyg_behavior_case.dto.responses;

import com.behavior.pyg_behavior_case.models.ProposalStatus;

import java.time.LocalDateTime;

public record CaseProposalResponse(
        Long id,
        Long professionalId,
        String approach,
        Integer estimatedPrice,
        Integer estimatedSessions,
        ProposalStatus status,
        LocalDateTime createdAt
){}