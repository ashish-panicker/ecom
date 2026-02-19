package com.example.authservice.security.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Successful Auth response", description = "Successful User authentication schema")
public record AuthResponse(
        String accessToken,
        String refreshToken
) {
}
