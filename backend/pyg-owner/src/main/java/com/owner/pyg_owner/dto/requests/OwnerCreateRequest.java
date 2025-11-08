package com.owner.pyg_owner.dto.requests;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;


public record OwnerCreateRequest (
        @NotBlank String phone,
        @NotBlank String address,
        @Past LocalDate birthDate
) { }