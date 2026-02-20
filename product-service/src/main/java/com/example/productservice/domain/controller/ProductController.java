package com.example.productservice.domain.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final Logger logger =
            LoggerFactory.getLogger(ProductController.class);

    /**
     * Client -> Product Service -> Auth Service [Current Stage]
     * Client -> Gateway -> Auth Service -> Product Service [Stage 2]
     */

    @Value("${eureka.instance.instance-id}")
    private String instanceId;

    @PostMapping("/status/{sku}")
    public ResponseEntity<String> status(
            @PathVariable String sku,
            @RequestHeader("Authorization") String token) {
        // Instance details that handled the response
        logger.debug("JWT: {}", token);
        return ResponseEntity.ok("Responsed by : " + instanceId + ". For request on: " + sku);
    }
}
