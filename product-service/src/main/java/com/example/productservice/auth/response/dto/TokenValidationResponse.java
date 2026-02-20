package com.example.productservice.auth.response.dto;

import java.util.List;

public record TokenValidationResponse(
        boolean valid,
        String username,
        List<String> roles,
        String message
) {
}
