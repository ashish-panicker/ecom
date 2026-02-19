package com.example.authservice.global.exceptions.auth;

public class DuplicateUserDetailsException extends UserAuthException {
    public DuplicateUserDetailsException(String s) {
        super(s);
    }
}
