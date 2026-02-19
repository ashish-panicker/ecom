package com.example.authservice.security.controller;

import com.example.authservice.security.dto.request.AuthRequest;
import com.example.authservice.security.dto.request.RegisterRequest;
import com.example.authservice.security.dto.response.AuthResponse;
import com.example.authservice.security.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        var user = authService.register(request);
        return user.getId().toString();
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        return authService.login(request);
    }
}
