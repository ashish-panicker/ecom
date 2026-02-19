package com.example.authservice.security.dto.request;

public record LogoutRequest(
        String accessToken,
        String refreshToken
) {
}
