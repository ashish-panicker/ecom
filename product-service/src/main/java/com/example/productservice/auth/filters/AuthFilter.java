package com.example.productservice.auth.filters;

import com.example.productservice.auth.client.AuthClient;
import com.example.productservice.auth.request.dto.TokenValidationRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    /**
     * Responsibilities
     * - Execute for every request
     * - Intercept the request
     * - Verify Authorization Request Header is present, if not cancel the request
     * - Extract the Bearer Token
     * - Forward the token to Auth Service for validation
     * - If valid, forward the request, if not cancel the request
     */

    private final AuthClient client;
    private final Logger logger = LoggerFactory.getLogger(AuthFilter.class);
    private final AuthClient authClient;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        logger.debug("Request reached the filter");

        logger.debug("Extracting Authorization header");
        var header = request.getHeader("Authorization");
        logger.debug("Header: {}", header);

        logger.debug("Checking if the header is properly formed");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            logger.error("Authorization header is not present");
            return;
        }

        String token = header.substring(7);
        logger.debug("Extracted the JWT: {}", token);

        logger.debug("Building the TokenValidationRequest: ");
        var validationRequest = new TokenValidationRequest(token);
        logger.debug("TokenValidationRequest: {}", validationRequest);

        logger.debug("Validating the token via AuthClient");
        var validationResponseEntity = authClient.validateToken(validationRequest);
        var validationResponse = validationResponseEntity.getBody();
        logger.debug("Validated token, response: {}", validationResponse);

        assert validationResponse != null;
        if (!validationResponse.valid()) {
            filterChain.doFilter(request, response);
            logger.error("Authorization token is not valid");
            return;
        }

        logger.debug("Loading the Roles");
        var authorities = validationResponse.roles().stream().map(SimpleGrantedAuthority::new).toList();

        logger.debug("Building an Authentication object");
        var authenticatedUser = UsernamePasswordAuthenticationToken
                .authenticated(
                        validationResponse.username(),
                        null,
                        authorities
                );

        logger.debug("Placing the Authentication object in the SecurityContextHolder");
        SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
        filterChain.doFilter(request, response);
    }
}
