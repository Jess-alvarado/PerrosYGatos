package com.owner.pyg_owner.dto.requests;

import com.owner.pyg_owner.validation.ValidPetType;

import io.micrometer.common.lang.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record PetRequest(
        @NotBlank String name,

        @NotBlank
        @ValidPetType
        @Schema(description = "Pet type", example = "DOG", allowableValues = {
        "DOG",
        "CAT" })
        String type,

        @NotBlank String breed,
        @Min(0) Integer age,
        @NotNull Boolean sterilized,
        @NotBlank String sex,
        @Nullable String behaviorDescription
) {}