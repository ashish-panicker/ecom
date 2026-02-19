package com.example.orderservice;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private RestClient.Builder restClientBuilder;

    @GetMapping("/verify/{sku}")
    public ResponseEntity<String> verify(@PathVariable String sku) {
        var response = restClientBuilder.build().get()
                .uri("http://PRODUCT-SERVICE/api/products/status/{sku}", sku)
                .retrieve().toEntity(String.class);
        return ResponseEntity.ok(response.getBody());
    }
}
