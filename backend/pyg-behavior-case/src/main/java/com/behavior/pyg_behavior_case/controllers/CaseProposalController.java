package com.behavior.pyg_behavior_case.controllers;

import com.behavior.pyg_behavior_case.dto.requests.CaseProposalRequest;
import com.behavior.pyg_behavior_case.dto.responses.CaseProposalResponse;
import com.behavior.pyg_behavior_case.exceptions.ForbiddenRoleException;
import com.behavior.pyg_behavior_case.services.CaseProposalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases")
@RequiredArgsConstructor
@Tag(name = "Case Proposals", description = "API for managing proposals on behavior cases")
public class CaseProposalController {

    private final CaseProposalService proposalService;

    @Operation(summary = "Submit a proposal for a case — professionals only")
    @PostMapping("/{caseId}/proposals")
    public ResponseEntity<CaseProposalResponse> propose(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long caseId,
            @Valid @RequestBody CaseProposalRequest request) {

        if (!"ROLE_PROFESSIONAL".equals(role)) {
            throw new ForbiddenRoleException("Only professionals can submit proposals");
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(proposalService.propose(
                        caseId, Long.parseLong(userId), request));
    }

    @Operation(summary = "Get all proposals for a case — owner only")
    @GetMapping("/{caseId}/proposals")
    public ResponseEntity<List<CaseProposalResponse>> getProposals(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long caseId) {

        if (!"ROLE_OWNER".equals(role)) {
            throw new ForbiddenRoleException("Only the case owner can view proposals");
        }

        return ResponseEntity.ok(
                proposalService.getProposals(caseId, Long.parseLong(userId)));
    }

    @Operation(summary = "Accept a proposal — owner only")
    @PatchMapping("/{caseId}/proposals/{proposalId}/accept")
    public ResponseEntity<CaseProposalResponse> acceptProposal(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long caseId,
            @PathVariable Long proposalId) {

        if (!"ROLE_OWNER".equals(role)) {
            throw new ForbiddenRoleException("Only the case owner can accept proposals");
        }

        return ResponseEntity.ok(
                proposalService.acceptProposal(
                        caseId, proposalId, Long.parseLong(userId)));
    }

    @Operation(summary = "Withdraw a proposal — professional only")
    @PatchMapping("/{caseId}/proposals/{proposalId}/withdraw")
    public ResponseEntity<CaseProposalResponse> withdrawProposal(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long caseId,
            @PathVariable Long proposalId) {

        if (!"ROLE_PROFESSIONAL".equals(role)) {
            throw new ForbiddenRoleException(
                    "Only the professional who submitted can withdraw");
        }

        return ResponseEntity.ok(
                proposalService.withdrawProposal(
                        caseId, proposalId, Long.parseLong(userId)));
    }
}