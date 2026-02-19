package com.example.authservice.global.exceptions.auth;

public class UserDoesNotExistsException extends UserAuthException {
    public UserDoesNotExistsException(String s) {
        super(s);
    }
}
