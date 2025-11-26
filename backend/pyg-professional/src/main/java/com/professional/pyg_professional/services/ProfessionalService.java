package com.professional.pyg_professional.services;

import org.springframework.stereotype.Service;

import com.professional.pyg_professional.clients.AuthServiceClient;
import com.professional.pyg_professional.dto.requests.ProfessionalRequest;
import com.professional.pyg_professional.dto.responses.ProfessionalResponse;
import com.professional.pyg_professional.models.ProfessionalProfile;
import com.professional.pyg_professional.repositories.ProfessionalRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfessionalService {

    private final AuthServiceClient authServiceClient;
    private final ProfessionalRepository professionalRepository;

    @Transactional
    public ProfessionalResponse createProfile(String bearerToken, ProfessionalRequest req) {
        var authRes = authServiceClient.validateToken(bearerToken);
        Long userId = authRes.getUserId();
        var professional = professionalRepository.findByUserId(userId)
                .orElseGet(() -> ProfessionalProfile.builder().userId(userId).build());
        professional.setPhone(req.phone());
        professional.setAddress(req.address());
        professional.setBirthDate(req.birthDate());
        professional.setProfession(req.profession());
        professional.setBio(req.bio());
        professional.setExperienceYears(req.experienceYears());
        professional.setPetTypes(req.petTypes());
        professional.setProfilePictureUrl(req.profilePictureUrl());
        professional.setInstagram(req.instagram());
        professional.setWebsite(req.website());
        professional.setAvailability(req.availability());
        professional.setStatus(req.status());

        var saved = professionalRepository.save(professional);
        return toResponse(saved);
    }

    private ProfessionalResponse toResponse(ProfessionalProfile p) {
        return new ProfessionalResponse(
                p.getId(),
                p.getUserId(),
                p.getPhone(),
                p.getAddress(),
                p.getBirthDate(),
                p.getProfession(),
                p.getBio(),
                p.getExperienceYears(),
                p.getPetTypes(),
                p.getRating(),
                p.getReviewCount(),
                p.getProfilePictureUrl(),
                p.getInstagram(),
                p.getWebsite(),
                p.getAvailability(),
                p.getStatus(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}