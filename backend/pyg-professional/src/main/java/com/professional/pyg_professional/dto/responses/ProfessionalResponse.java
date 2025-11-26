package com.professional.pyg_professional.dto.responses;

import java.time.LocalDate;

public record ProfessionalResponse(
        Long id,
        Long userId,

        // Contact Info
        String phone,
        String address,

        LocalDate birthDate,

        // Professional Info
        String profession,
        String bio,
        Integer experienceYears,
        String petTypes,

        // Reputation
        Double rating,
        Integer reviewCount,

        // Social Media
        String profilePictureUrl,
        String instagram,
        String website,

        // Availability
        String availability,

        // Status: ACTIVE, PENDING, SUSPENDED
        String status,

        LocalDate createdAt,
        LocalDate updatedAt
){}