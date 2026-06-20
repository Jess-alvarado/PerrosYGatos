package com.owner.pyg_owner.exceptions;

public class ForbiddenRoleException extends RuntimeException {
    public ForbiddenRoleException(String message) {
        super(message);
    }
}