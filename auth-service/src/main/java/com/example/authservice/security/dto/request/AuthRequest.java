package com.example.authservice.security.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Auth schema", description = "User authentication schema")
public record AuthRequest(
        @Schema(description = "Valid username of the user") String username,
        @Schema(description = "Valid password of the user") String password
) {}
