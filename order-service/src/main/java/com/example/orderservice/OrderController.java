package com.example.orderservice;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private RestClient.Builder restClientBuilder;

    @PostMapping("/verify/{sku}")
    public ResponseEntity<String> verify(@PathVariable String sku) {
        var response = restClientBuilder.build()
                .post()
                .uri("http://PRODUCT-SERVICE/api/products/status/{sku}", sku)
                .retrieve().toEntity(String.class);
        return ResponseEntity.ok(response.getBody());
    }
}
