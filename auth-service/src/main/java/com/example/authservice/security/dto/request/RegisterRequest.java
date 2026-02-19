package com.example.authservice.security.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Register schema", description = "User registration schema")
public record RegisterRequest(
        @Schema(description = "Username of the user") String username,
        @Schema(description = "Strong password") String password,
        @Schema(description = "Official email id") String email,
        @Schema(description = "By default ROLE_USER") String role
) {
}
