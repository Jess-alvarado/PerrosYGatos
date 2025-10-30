package com.owner.pyg_owner.dto.requests;

import jakarta.validation.constraints.*;

public record PetRequest(
        @NotBlank String name,
        @NotBlank String type,
        @NotBlank String breed,
        @Min(0) Integer age,
        @NotNull Boolean sterilized,
        @NotBlank String sex,
        String behaviorDescription
) {}