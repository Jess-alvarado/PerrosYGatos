package com.owner.pyg_owner.dto.responses;

import java.time.LocalDate;

public record OwnerResponse(
        Long id,
        Long userId,
        String phone,
        String address,
        LocalDate birthDate
) { }