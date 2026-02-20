package com.example.productservice.auth.client;

import com.example.productservice.auth.request.dto.TokenValidationRequest;
import com.example.productservice.auth.response.dto.TokenValidationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "AUTH-SERVICE")
public interface AuthClient {

    @PostMapping("/api/auth/validate")
    ResponseEntity<TokenValidationResponse> validateToken(TokenValidationRequest request);

}
