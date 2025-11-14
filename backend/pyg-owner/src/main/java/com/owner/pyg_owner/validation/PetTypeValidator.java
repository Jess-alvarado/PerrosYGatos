package com.owner.pyg_owner.validation;

import com.owner.pyg_owner.models.PetType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PetTypeValidator implements ConstraintValidator<ValidPetType, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Si es null o vacío, dejamos que @NotBlank se encargue
        // Retornamos true aquí para evitar mensajes de error duplicados
        if (value == null || value.isBlank()) {
            return true;
        }

        try {
            PetType.valueOf(value.toUpperCase());
            return true; // Es un valor válido del enum
        } catch (IllegalArgumentException ex) {
            return false; // No existe en el enum → inválido
        }
    }
}