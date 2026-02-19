package com.example.authservice.global.exceptions.auth;

public class UserAuthException extends RuntimeException{
    public UserAuthException(String message) {
        super(message);
    }
}
