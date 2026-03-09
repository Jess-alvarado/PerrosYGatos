package com.professional.pyg_professional.dto.requests;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public record ProfessionalUpdateRequest(
        String phone,
        String address,

        @Past
        LocalDate birthDate,

        String profession,

        @Size(max = 700)
        String bio,

        @Min(0)
        Integer experienceYears,

        String petTypes,
        String profilePictureUrl,
        String instagram,
        String website,
        String availability
) { }