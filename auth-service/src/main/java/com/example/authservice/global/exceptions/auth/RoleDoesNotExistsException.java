package com.example.authservice.global.exceptions.auth;

public class RoleDoesNotExistsException extends UserAuthException {
    public RoleDoesNotExistsException(String s) {
        super(s);
    }
}
