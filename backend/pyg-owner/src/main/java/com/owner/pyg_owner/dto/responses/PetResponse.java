package com.owner.pyg_owner.dto.responses;

public record PetResponse(
        Long id,
        String name,
        String type,
        String breed,
        Integer age,
        Boolean sterilized,
        String sex,
        String behaviorDescription
) { }