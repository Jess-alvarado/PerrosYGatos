package com.professional.pyg_professional.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.professional.pyg_professional.clients.AuthServiceClient;
import com.professional.pyg_professional.dto.requests.ProfessionalRequest;
import com.professional.pyg_professional.dto.requests.ProfessionalUpdateRequest;
import com.professional.pyg_professional.dto.responses.ProfessionalResponse;
import com.professional.pyg_professional.dto.responses.TokenValidationResponse;
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
        TokenValidationResponse authRes = validateToken(bearerToken);
        validateProfessionalRole(authRes);

        Long userId = authRes.getUserId();

        if (professionalRepository.existsByUserId(userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Professional profile already exists");
        }

        ProfessionalProfile professional = ProfessionalProfile.builder()
                .userId(userId)
                .phone(req.phone())
                .address(req.address())
                .birthDate(req.birthDate())
                .profession(req.profession())
                .bio(req.bio())
                .experienceYears(req.experienceYears())
                .petTypes(req.petTypes())
                .profilePictureUrl(req.profilePictureUrl())
                .instagram(req.instagram())
                .website(req.website())
                .availability(req.availability())
                .status("ACTIVE")
                .rating(0.0)
                .reviewCount(0)
                .build();

        ProfessionalProfile saved = professionalRepository.save(professional);
        return toResponse(saved);
    }

    public ProfessionalResponse getMyProfile(String bearerToken) {
        TokenValidationResponse authRes = validateToken(bearerToken);
        validateProfessionalRole(authRes);

        ProfessionalProfile profile = professionalRepository.findByUserId(authRes.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professional profile not found"));

        return toResponse(profile);
    }

    @Transactional
    public ProfessionalResponse updateMyProfile(String bearerToken, ProfessionalUpdateRequest req) {
        TokenValidationResponse authRes = validateToken(bearerToken);
        validateProfessionalRole(authRes);

        ProfessionalProfile profile = professionalRepository.findByUserId(authRes.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professional profile not found"));

        if (req.phone() != null) {
            profile.setPhone(req.phone());
        }
        if (req.address() != null) {
            profile.setAddress(req.address());
        }
        if (req.birthDate() != null) {
            profile.setBirthDate(req.birthDate());
        }
        if (req.profession() != null) {
            profile.setProfession(req.profession());
        }
        if (req.bio() != null) {
            profile.setBio(req.bio());
        }
        if (req.experienceYears() != null) {
            profile.setExperienceYears(req.experienceYears());
        }
        if (req.petTypes() != null) {
            profile.setPetTypes(req.petTypes());
        }
        if (req.profilePictureUrl() != null) {
            profile.setProfilePictureUrl(req.profilePictureUrl());
        }
        if (req.instagram() != null) {
            profile.setInstagram(req.instagram());
        }
        if (req.website() != null) {
            profile.setWebsite(req.website());
        }
        if (req.availability() != null) {
            profile.setAvailability(req.availability());
        }

        ProfessionalProfile saved = professionalRepository.save(profile);
        return toResponse(saved);
    }

    public List<ProfessionalResponse> getAllProfessionals(String bearerToken) {
        validateToken(bearerToken);

        return professionalRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ProfessionalResponse getProfessionalById(String bearerToken, Long id) {
        validateToken(bearerToken);

        ProfessionalProfile profile = professionalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professional profile not found"));

        return toResponse(profile);
    }

    private TokenValidationResponse validateToken(String bearerToken) {
        TokenValidationResponse authRes = authServiceClient.validateToken(bearerToken);

        if (authRes == null || !authRes.isValid()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }

        return authRes;
    }

    private void validateProfessionalRole(TokenValidationResponse authRes) {
        if (!"ROLE_PROFESSIONAL".equals(authRes.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have the PROFESSIONAL role");
        }
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