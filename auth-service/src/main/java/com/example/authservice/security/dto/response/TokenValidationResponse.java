package com.example.authservice.security.dto.response;

import java.util.List;

public record TokenValidationResponse(
        boolean valid,
        String username,
        List<String> roles,
        String message
) {
}
