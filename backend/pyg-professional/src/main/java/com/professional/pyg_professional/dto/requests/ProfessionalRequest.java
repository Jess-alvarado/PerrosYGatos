package com.professional.pyg_professional.dto.requests;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

public record ProfessionalRequest (
        @NotBlank String phone,
        @NotBlank String address,
        @Past LocalDate birthDate
) { }