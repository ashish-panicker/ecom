package com.example.productservice.auth.service;

import com.example.productservice.auth.client.AuthClient;
import com.example.productservice.auth.request.dto.TokenValidationRequest;
import com.example.productservice.auth.response.dto.TokenValidationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthClient authClient;

    public TokenValidationResponse validateToken(TokenValidationRequest request) {
        var response = authClient.validateToken(request);
        return response.getBody();
    }

}
