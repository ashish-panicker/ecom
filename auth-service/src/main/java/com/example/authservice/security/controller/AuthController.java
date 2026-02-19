package com.example.authservice.security.controller;

import com.example.authservice.security.dto.request.AuthRequest;
import com.example.authservice.security.dto.request.RegisterRequest;
import com.example.authservice.security.dto.response.AuthResponse;
import com.example.authservice.security.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authorization API", description = "Secured api for performing registration, login and verification")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Create a new account in the ecom system",
            security = @SecurityRequirement(name = "None")
    )
    public String register(@RequestBody RegisterRequest request) {
        var user = authService.register(request);
        return user.getId().toString();
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        return authService.login(request);
    }
}
