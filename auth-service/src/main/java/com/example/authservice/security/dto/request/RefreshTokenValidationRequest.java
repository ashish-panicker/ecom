package com.example.authservice.security.dto.request;

public record RefreshTokenValidationRequest(
        String refreshToken
) {
}
