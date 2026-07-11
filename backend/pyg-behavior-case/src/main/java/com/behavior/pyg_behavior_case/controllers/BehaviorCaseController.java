package com.behavior.pyg_behavior_case.controllers;



import com.behavior.pyg_behavior_case.dto.requests.BehaviorCaseRequest;
import com.behavior.pyg_behavior_case.dto.responses.BehaviorCaseResponse;
import com.behavior.pyg_behavior_case.exceptions.ForbiddenRoleException;
import com.behavior.pyg_behavior_case.models.CaseStatus;
import com.behavior.pyg_behavior_case.services.BehaviorCaseService;
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
@Tag(name = "Behavior Cases", description = "API for managing pet behavior cases")
public class BehaviorCaseController {

    private final BehaviorCaseService caseService;

    @Operation(summary = "Create a new behavior case")
    @PostMapping
    public ResponseEntity<BehaviorCaseResponse> createCase(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody BehaviorCaseRequest request) {

        if (!"ROLE_OWNER".equals(role)) {
            throw new ForbiddenRoleException(
                    "Only owners can create cases");
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(caseService.createCase(Long.parseLong(userId), request));
    }

    @Operation(summary = "Get all open cases — feed for professionals")
    @GetMapping
    public ResponseEntity<List<BehaviorCaseResponse>> getOpenCases(
            @RequestHeader("X-User-Role") String role) {

        if (!"ROLE_PROFESSIONAL".equals(role)) {
            throw new ForbiddenRoleException(
                    "Only professionals can browse open cases");
        }

        return ResponseEntity.ok(caseService.getOpenCases());
    }

    @Operation(summary = "Get my cases — for owners")
    @GetMapping("/my")
    public ResponseEntity<List<BehaviorCaseResponse>> getMyCases(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {

        if (!"ROLE_OWNER".equals(role)) {
            throw new ForbiddenRoleException(
                    "Only owners can view their cases");
        }

        return ResponseEntity.ok(caseService.getMyCases(Long.parseLong(userId)));
    }

    @Operation(summary = "Get case detail by ID")
    @GetMapping("/{id}")
    public ResponseEntity<BehaviorCaseResponse> getCaseById(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long id) {

        return ResponseEntity.ok(caseService.getCaseById(id));
    }

    @Operation(summary = "Update case status")
    @PatchMapping("/{id}/status")
    public ResponseEntity<BehaviorCaseResponse> updateStatus(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long id,
            @RequestParam CaseStatus newStatus) {

        if (!"ROLE_OWNER".equals(role)) {
            throw new ForbiddenRoleException(
                    "Only owners can change case status");
        }

        return ResponseEntity.ok(
                caseService.updateStatus(id, Long.parseLong(userId), newStatus));
    }
}