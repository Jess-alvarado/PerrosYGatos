package com.professional.pyg_professional.dto.requests;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public record ProfessionalRequest(
        @NotBlank String phone,
        @NotBlank String address,
        @Past LocalDate birthDate,
        @NotBlank String profession,

        @NotBlank
        @Size(max = 700)
        String bio,

        @Min(0) Integer experienceYears,

        @NotBlank String petTypes,
        String profilePictureUrl,
        String instagram,
        String website,

        @NotBlank String availability
) { }