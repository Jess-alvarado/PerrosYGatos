package com.owner.pyg_owner.dto.requests;

import java.time.LocalDate;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;


public record OwnerCreateRequest (
        @NotBlank String phone,
        @NotBlank String address,
        @Past LocalDate birthDate,
        @Valid @Size(min = 0) List<PetRequest> pets
) { }
