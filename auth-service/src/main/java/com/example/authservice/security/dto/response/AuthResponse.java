package com.example.authservice.security.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Successful Auth response", description = "Successful User authentication schema")
public record AuthResponse(
        String accessToken,
        String refreshToken
) {
}

/**
 * JWT:         [19/02/2026 9:00 AM - 19/02/2026 9:15 AM]
 * REFRESH:     [19/02/2026 9:00 AM - 20/02/2026 9:00 AM]
 */
