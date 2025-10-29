package com.owner.pyg_owner.services;

import com.owner.pyg_owner.clients.JwtReader;
import com.owner.pyg_owner.dto.requests.OwnerCreateRequest;
import com.owner.pyg_owner.dto.responses.OwnerResponse;
import com.owner.pyg_owner.dto.responses.PetResponse;
import com.owner.pyg_owner.models.*;
import com.owner.pyg_owner.repositories.OwnerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerRepository ownerRepo;
    private final JwtReader jwtReader; // to extract userId from the token

    @Transactional
    // Create or update the profile of the authenticated owner
    public OwnerResponse createOrUpdateProfile(String bearerToken, OwnerCreateRequest req) {
        Long userId = jwtReader.extractUserId(bearerToken); // read “uid” from the JWT

        var owner = ownerRepo.findByUserId(userId)
                .orElseGet(() -> OwnerProfile.builder().userId(userId).build());

        owner.setPhone(req.phone());
        owner.setAddress(req.address());
        owner.setBirthDate(req.birthDate());

        // Clean and reload pets
        owner.getPets().clear();
        if (req.pets() != null) {
            for (var p : req.pets()) {
                var pet = Pet.builder()
                        .name(p.name())
                        .type(PetType.valueOf(p.type()))
                        .breed(p.breed())
                        .age(p.age())
                        .sterilized(p.sterilized())
                        .sex(p.sex())
                        .behaviorDescription(p.behaviorDescription())
                        .owner(owner)
                        .build();
                owner.getPets().add(pet);
            }
        }

        var saved = ownerRepo.save(owner);
        return toResponse(saved);
    }

    // Retrieve the profile of the authenticated owner
    public OwnerResponse getMyProfile(String bearerToken) {
        Long userId = jwtReader.extractUserId(bearerToken);
        var owner = ownerRepo.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Owner profile not found"));
        return toResponse(owner);
    }

    // Helper to convert OwnerProfile to OwnerResponse
    private OwnerResponse toResponse(OwnerProfile o) {
        var petRes = o.getPets().stream()
                .map(p -> new PetResponse(
                        p.getId(), p.getName(), p.getType().name(),
                        p.getBreed(), p.getAge(), p.getSterilized(),
                        p.getSex(), p.getBehaviorDescription()))
                .collect(Collectors.toList());

        return new OwnerResponse(
                o.getId(), o.getUserId(), o.getPhone(), o.getAddress(),
                o.getBirthDate(), petRes);
    }
}
