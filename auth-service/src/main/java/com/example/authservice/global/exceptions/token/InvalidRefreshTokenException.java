package com.example.authservice.global.exceptions.token;

public class InvalidRefreshTokenException extends JwtTokenException {
    public InvalidRefreshTokenException(String invalidRefreshToken) {
        super(invalidRefreshToken);
    }
}
