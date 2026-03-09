package com.professional.pyg_professional.dto.responses;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing the data of a professional profile")
public record ProfessionalResponse(

        @Schema(description = "Internal ID of the professional profile", example = "1")
        Long id,

        @Schema(description = "User ID from the authentication service", example = "12")
        Long userId,

        @Schema(description = "Contact phone number", example = "+56912345678")
        String phone,

        @Schema(description = "Professional address or location", example = "Linares, Chile")
        String address,

        @Schema(description = "Birth date", example = "1998-05-10")
        LocalDate birthDate,

        @Schema(description = "Profession or specialization", example = "Feline ethologist")
        String profession,

        @Schema(description = "Professional biography", example = "Specialist in feline behavior and environmental enrichment")
        String bio,

        @Schema(description = "Years of experience", example = "4")
        Integer experienceYears,

        @Schema(description = "Types of pets handled by the professional", example = "CAT")
        String petTypes,

        @Schema(description = "Average rating", example = "0.0")
        Double rating,

        @Schema(description = "Number of reviews", example = "0")
        Integer reviewCount,

        @Schema(description = "Profile picture URL", example = "https://mysite.com/photo.jpg")
        String profilePictureUrl,

        @Schema(description = "Instagram account", example = "jess_ethologist")
        String instagram,

        @Schema(description = "Professional website", example = "https://mysite.com")
        String website,

        @Schema(description = "Professional availability", example = "Mon-Fri 10:00-18:00")
        String availability,

        @Schema(description = "Profile status", example = "ACTIVE")
        String status,

        @Schema(description = "Profile creation date", example = "2026-03-09")
        LocalDate createdAt,

        @Schema(description = "Last profile update date", example = "2026-03-09")
        LocalDate updatedAt
) {}