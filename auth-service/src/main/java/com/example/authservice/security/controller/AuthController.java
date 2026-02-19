package com.example.authservice.security.controller;

import com.example.authservice.security.dto.request.AuthRequest;
import com.example.authservice.security.dto.request.RegisterRequest;
import com.example.authservice.security.dto.request.TokenValidationRequest;
import com.example.authservice.security.dto.response.AuthResponse;
import com.example.authservice.security.dto.response.TokenValidationResponse;
import com.example.authservice.security.service.AuthService;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @Operation(
            summary = "Login and auhtneticate your credentials",
            description = "Login to the endpoint with credentials, on successfull login jwt and refrehs token will be shared",
            security = @SecurityRequirement(name = "None")
    )
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/validate")
    public ResponseEntity<TokenValidationResponse> validateToken(@RequestBody TokenValidationRequest request) {
        var response = authService.validateToken(request);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<TokenValidationResponse> expiredJwtException(ExpiredJwtException ex) {
        var response =
                new TokenValidationResponse(false, null, null,
                        ex.getMessage());
        return ResponseEntity.internalServerError().body(response);
    }
}
