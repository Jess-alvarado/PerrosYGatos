package com.owner.pyg_owner.services;

import com.owner.pyg_owner.dto.requests.OwnerCreateRequest;
import com.owner.pyg_owner.dto.requests.OwnerUpdateRequest;
import com.owner.pyg_owner.dto.responses.OwnerResponse;
import com.owner.pyg_owner.exceptions.AlreadyExistsException;
import com.owner.pyg_owner.exceptions.NotFoundException;
import com.owner.pyg_owner.exceptions.ValidationException;
import com.owner.pyg_owner.models.OwnerProfile;
import com.owner.pyg_owner.repositories.OwnerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerRepository ownerRepo;

    @Transactional
    public OwnerResponse createProfile(Long userId, OwnerCreateRequest req) {
        if (ownerRepo.findByUserId(userId).isPresent()) {
            throw new AlreadyExistsException("Owner profile already exists");
        }

        var owner = OwnerProfile.builder()
                .userId(userId)
                .phone(req.phone())
                .address(req.address())
                .birthDate(req.birthDate())
                .build();

        try {
            var saved = ownerRepo.save(owner);
            return toResponse(saved);
        } catch (Exception ex) {
            throw new ValidationException("Error creating profile: Invalid data provided");
        }
    }

    @Transactional
    public OwnerResponse updateProfile(Long userId, OwnerUpdateRequest req) {
        var owner = ownerRepo.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Owner profile not found"));

        if (req.phone() != null) owner.setPhone(req.phone());
        if (req.address() != null) owner.setAddress(req.address());
        if (req.birthDate() != null) owner.setBirthDate(req.birthDate());

        try {
            var saved = ownerRepo.save(owner);
            return toResponse(saved);
        } catch (Exception ex) {
            throw new ValidationException("Error updating profile: Invalid data provided");
        }
    }

    public OwnerResponse getMyProfile(Long userId) {
        var owner = ownerRepo.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Owner profile not found"));

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