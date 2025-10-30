package com.owner.pyg_owner.controllers;

import com.owner.pyg_owner.services.OwnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.owner.pyg_owner.dto.requests.OwnerCreateRequest;
import com.owner.pyg_owner.dto.responses.OwnerResponse;

@RestController
@RequestMapping("/owners")
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;

    @PostMapping
    public ResponseEntity<OwnerResponse> upsertProfile(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody OwnerCreateRequest request
    ) {
        return ResponseEntity.ok(ownerService.createOrUpdateProfile(authorization, request));
    }

    @GetMapping("/me")
    public ResponseEntity<OwnerResponse> getMyProfile(
            @RequestHeader("Authorization") String authorization
    ) {
        return ResponseEntity.ok(ownerService.getMyProfile(authorization));
    }
}
