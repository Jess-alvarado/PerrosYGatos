package com.professional.pyg_professional.dto.requests;

import java.time.LocalDate;

import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.*;

public record ProfessionalRequest (
        @NotBlank String phone,
        @NotBlank String address,
        @Past LocalDate birthDate,
        @NotBlank String profession,

        @Size(max = 700)
        @NotBlank String bio,

        @Min(0) Integer experienceYears,

        @NotBlank String petTypes,
        String profilePictureUrl,

        @Nullable String instagram,
        @Nullable String website,
        @NotBlank String availability,
        @NotBlank String status
) { }