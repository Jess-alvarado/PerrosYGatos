package com.professional.pyg_professional.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.professional.pyg_professional.dto.requests.ProfessionalRequest;
import com.professional.pyg_professional.dto.requests.ProfessionalUpdateRequest;
import com.professional.pyg_professional.dto.responses.ProfessionalResponse;
import com.professional.pyg_professional.exceptions.AlreadyExistsException;
import com.professional.pyg_professional.exceptions.NotFoundException;
import com.professional.pyg_professional.exceptions.ValidationException;
import com.professional.pyg_professional.models.ProfessionalProfile;
import com.professional.pyg_professional.repositories.ProfessionalRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfessionalService {

    private final ProfessionalRepository professionalRepository;

    @Transactional
    public ProfessionalResponse createProfile(Long userId, ProfessionalRequest req) {
        if (professionalRepository.existsByUserId(userId)) {
            throw new AlreadyExistsException("Professional profile already exists");
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

        try {
            ProfessionalProfile saved = professionalRepository.save(professional);
            return toResponse(saved);
        } catch (Exception ex) {
            throw new ValidationException("Error creating professional profile: Invalid data provided");
        }
    }

    public ProfessionalResponse getMyProfile(Long userId) {
        ProfessionalProfile profile = professionalRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Professional profile not found"));

        return toResponse(profile);
    }

    @Transactional
    public ProfessionalResponse updateMyProfile(Long userId, ProfessionalUpdateRequest req) {
        ProfessionalProfile profile = professionalRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Professional profile not found"));

        if (req.phone() != null) profile.setPhone(req.phone());
        if (req.address() != null) profile.setAddress(req.address());
        if (req.birthDate() != null) profile.setBirthDate(req.birthDate());
        if (req.profession() != null) profile.setProfession(req.profession());
        if (req.bio() != null) profile.setBio(req.bio());
        if (req.experienceYears() != null) profile.setExperienceYears(req.experienceYears());
        if (req.petTypes() != null) profile.setPetTypes(req.petTypes());
        if (req.profilePictureUrl() != null) profile.setProfilePictureUrl(req.profilePictureUrl());
        if (req.instagram() != null) profile.setInstagram(req.instagram());
        if (req.website() != null) profile.setWebsite(req.website());
        if (req.availability() != null) profile.setAvailability(req.availability());

        try {
            ProfessionalProfile saved = professionalRepository.save(profile);
            return toResponse(saved);
        } catch (Exception ex) {
            throw new ValidationException("Error updating professional profile: Invalid data provided");
        }
    }

    public List<ProfessionalResponse> getAllProfessionals() {
        return professionalRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ProfessionalResponse getProfessionalById(Long id) {
        ProfessionalProfile profile = professionalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Professional profile not found"));

        return toResponse(profile);
    }

    private ProfessionalResponse toResponse(ProfessionalProfile p) {
        return new ProfessionalResponse(
                p.getId(), p.getUserId(), p.getPhone(), p.getAddress(),
                p.getBirthDate(), p.getProfession(), p.getBio(),
                p.getExperienceYears(), p.getPetTypes(), p.getRating(),
                p.getReviewCount(), p.getProfilePictureUrl(), p.getInstagram(),
                p.getWebsite(), p.getAvailability(), p.getStatus(),
                p.getCreatedAt(), p.getUpdatedAt()
        );
    }
}