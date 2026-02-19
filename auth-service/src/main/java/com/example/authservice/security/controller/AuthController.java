package com.example.authservice.security.controller;

import com.example.authservice.security.dto.request.*;
import com.example.authservice.security.dto.response.AuthResponse;
import com.example.authservice.security.dto.response.TokenValidationResponse;
import com.example.authservice.security.service.AuthService;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenValidationRequest request) {
        var response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    /**
     * A user has logged in at 10:00 AM
     * Access token validity is 1 hour, refresh token validity is 24 hours.
     * At 10:15 AM the user logs out.
     *
     * A user who has an access token valid for 1 hour and a valid refresh token has logged out.
     * During logout our system revokes/deletes the refresh token.
     *
     * After logout  can the same user access our secured endpoints with the existing access token?
     *
     * If yes, how will you ensure after logout no access to secured endpoints are made irrespective
     * of even having a valid access token?
     *
     * Two proposals
     * 1. Reduce the validity time of access token to 5 - 10 minutes.
     * 2. During logout, send both the access and refresh tokens to the server.
     *      Refresh token will be revoked
     *      Access token can be blacklisted and maintained in a fast, in memory database, like redis
     */

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(@RequestBody LogoutRequest request) {
        var response = authService.logout(request);
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
