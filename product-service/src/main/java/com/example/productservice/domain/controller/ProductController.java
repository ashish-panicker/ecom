package com.example.productservice.domain.controller;

import com.example.productservice.auth.request.dto.TokenValidationRequest;
import com.example.productservice.auth.response.dto.TokenValidationResponse;
import com.example.productservice.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final Logger logger =
            LoggerFactory.getLogger(ProductController.class);
    private final AuthService authService;

    /**
     * Client -> Product Service -> Auth Service [Current Stage]
     * Client -> Gateway -> Auth Service -> Product Service [Stage 2]
     *
     */

    @Value("${eureka.instance.instance-id}")
    private String instanceId;

    @PostMapping("/status/{sku}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> status(
            @PathVariable String sku) {
        // Instance details that handled the response
        return ResponseEntity.ok("Responsed by : " + instanceId + ". For request on: " + sku);
    }
}
