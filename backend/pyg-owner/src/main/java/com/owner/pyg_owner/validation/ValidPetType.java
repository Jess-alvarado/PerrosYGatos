package com.owner.pyg_owner.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = PetTypeValidator.class)
public @interface ValidPetType {

    String message() default "Invalid pet type. Allowed values: DOG, CAT.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
