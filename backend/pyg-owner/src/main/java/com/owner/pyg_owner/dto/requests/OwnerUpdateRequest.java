package com.owner.pyg_owner.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Request to partially update an owner profile")
public record OwnerUpdateRequest(

        @Schema(description = "Phone number", example = "+56912345678")
        String phone,

        @Schema(description = "Address", example = "Calle 3, Lenga")
        String address,

        @Schema(description = "Birth date", example = "1998-05-10")
        LocalDate birthDate
) {
}