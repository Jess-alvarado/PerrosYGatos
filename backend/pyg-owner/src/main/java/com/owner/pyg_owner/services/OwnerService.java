package com.owner.pyg_owner.services;

import com.owner.pyg_owner.clients.AuthServiceClient;
import com.owner.pyg_owner.dto.requests.OwnerCreateRequest;
import com.owner.pyg_owner.dto.responses.OwnerResponse;
import com.owner.pyg_owner.models.OwnerProfile;
import com.owner.pyg_owner.repositories.OwnerRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerRepository ownerRepo;
    private final AuthServiceClient authServiceClient;

    /**
     * Create or update the profile of the authenticated owner.
     * If the profile does not exist, it will be created.
     */
    @Transactional
    public OwnerResponse createOrUpdateProfile(String bearerToken, OwnerCreateRequest req) {
        var authRes = authServiceClient.validateToken(bearerToken);
        Long userId = authRes.getUserId();
        var owner = ownerRepo.findByUserId(userId)
                .orElseGet(() -> OwnerProfile.builder().userId(userId).build());
        owner.setPhone(req.phone());
        owner.setAddress(req.address());
        owner.setBirthDate(req.birthDate());

        var saved = ownerRepo.save(owner);
        return toResponse(saved);
    }

    /**
     * Retrieve the profile of the authenticated owner.
     */
    public OwnerResponse getMyProfile(String bearerToken) {
        var authRes = authServiceClient.validateToken(bearerToken);
        Long userId = authRes.getUserId();

        // Find profile by userId or throw 404
        var owner = ownerRepo.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Owner profile not found"));

        return toResponse(owner);
    }

    private OwnerResponse toResponse(OwnerProfile o) {

        return new OwnerResponse(
                o.getId(),
                o.getUserId(),
                o.getPhone(),
                o.getAddress(),
                o.getBirthDate()
        );
    }
}
