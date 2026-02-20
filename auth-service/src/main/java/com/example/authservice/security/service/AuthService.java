package com.example.authservice.security.service;

import com.example.authservice.global.exceptions.auth.DuplicateUserDetailsException;
import com.example.authservice.global.exceptions.auth.RoleDoesNotExistsException;
import com.example.authservice.global.exceptions.auth.UserDoesNotExistsException;
import com.example.authservice.global.exceptions.token.InvalidRefreshTokenException;
import com.example.authservice.security.domain.entity.RefreshToken;
import com.example.authservice.security.domain.entity.Role;
import com.example.authservice.security.domain.entity.User;
import com.example.authservice.security.domain.repo.RefreshTokenRepository;
import com.example.authservice.security.domain.repo.RoleRepository;
import com.example.authservice.security.domain.repo.UserRepository;
import com.example.authservice.security.dto.request.*;
import com.example.authservice.security.dto.response.AuthResponse;
import com.example.authservice.security.dto.response.TokenValidationResponse;
import com.example.authservice.security.service.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenBlackListService blackListService;

    /**
     * @Transactional Transactions are Logical Unit Of Work, a set of statements executed to perform one action.
     * Atomicity, all or none
     * Consistency, data must be in consistent format
     * Isolation, one transaction does not affect the other
     * Durability, committed changes survives database crashes
     * <p>
     * propagation [REQUIRED, REQUIRED_NEW, SUPPORTS, NOT_SUPPORTED, NEVER, NESTED]
     * <p>
     * Transactions are automatically rolled back in case a RuntimeException is thrown
     */
    @Transactional(rollbackFor = {DuplicateUserDetailsException.class, RoleDoesNotExistsException.class})
    public User register(RegisterRequest request) {
        userRepository.findByUsernameOrEmail(request.username(), request.email())
                .ifPresent(user -> {
                    throw new DuplicateUserDetailsException(user.getUsername() + " already exists.");
                });
        Role role = roleRepository.findByName(request.role())
                .orElseThrow(() -> new RoleDoesNotExistsException("Role " + request.role() + " not present."));

        User user = new User(
                request.username(),
                request.email(),
                passwordEncoder.encode(request.password()),
                Set.of(role)
        );
        return userRepository.save(user);
    }

    @Transactional
    public AuthResponse login(AuthRequest request) {

        var user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UserDoesNotExistsException("User " + request.username() + " not present."));
        ;

        var unauthenticatedUser = new UsernamePasswordAuthenticationToken(
                request.username(), request.password()
        );
        var authenticated = authenticationManager.authenticate(unauthenticatedUser);

        String token = jwtService.generateToken(authenticated);
        String refreshToken = jwtService.generateRefreshToken(authenticated.getName());
        RefreshToken refreshTokenEntity = new RefreshToken(
                refreshToken,
                Instant.now().plusSeconds(86400),
                false,
                user
        );
        var ignored = refreshTokenRepository.save(refreshTokenEntity);
        return new AuthResponse(
                token, refreshToken
        );
    }

    public TokenValidationResponse validateToken(TokenValidationRequest request) {
        if (jwtService.isTokenExpired(request.token())) {
            return new TokenValidationResponse(false, null, null, "Invalid token");
        }
        return new TokenValidationResponse(
                true,
                jwtService.extractUserName(request.token()),
                jwtService.extractRoles(request.token()),
                "Token validated"
        );
    }

    public AuthResponse refreshToken(RefreshTokenValidationRequest request) {
        var refreshToken = refreshTokenRepository
                .findByToken(request.refreshToken())
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));
        if (refreshToken.isRevoked()) {
            throw new InvalidRefreshTokenException("Revoked refresh token");
        }
        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new InvalidRefreshTokenException("Expired refresh token");
        }
        var user = refreshToken.getUser();
        var roles = user.getRoles().stream().map(Role::getName).toList();
        String accessToken = jwtService.generateToken(user.getUsername(), roles);
        return new AuthResponse(accessToken, refreshToken.getToken());
    }

    @Transactional
    public AuthResponse logout(LogoutRequest request) throws NoSuchAlgorithmException {

        // Revoke the refresh token
        var refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        // Blacklist the access token
        var expirationDate = jwtService.extractAllClaims(request.accessToken()).getExpiration();
        var expirationInMillis = expirationDate.getTime() - System.currentTimeMillis();
        blackListService.blackListToken(request.accessToken(), expirationInMillis);

        return new AuthResponse("nill", "nil");
    }
}
