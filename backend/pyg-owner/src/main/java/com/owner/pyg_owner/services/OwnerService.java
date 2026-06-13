package com.owner.pyg_owner.services;

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

    @Transactional
    public OwnerResponse createOrUpdateProfile(Long userId, OwnerCreateRequest req) {
        var owner = ownerRepo.findByUserId(userId)
                .orElseGet(() -> OwnerProfile.builder().userId(userId).build());

        owner.setPhone(req.phone());
        owner.setAddress(req.address());
        owner.setBirthDate(req.birthDate());

        var saved = ownerRepo.save(owner);
        return toResponse(saved);
    }

    public OwnerResponse getMyProfile(Long userId) {
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