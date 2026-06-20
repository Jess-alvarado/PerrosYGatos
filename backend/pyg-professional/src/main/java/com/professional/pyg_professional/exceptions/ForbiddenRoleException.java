package com.professional.pyg_professional.exceptions;

public class ForbiddenRoleException extends RuntimeException {
    public ForbiddenRoleException(String message) {
        super(message);
    }
}