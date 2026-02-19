package com.example.authservice.security.dto.request;

public record AuthRequest(
        String username,
        String password
) {}
