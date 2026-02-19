package com.example.authservice.security.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {
}
