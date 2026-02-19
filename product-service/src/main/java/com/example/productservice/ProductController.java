package com.example.productservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Value("${eureka.instance.instance-id}")
    private String instanceId;

    @GetMapping("/status/{sku}")
    public ResponseEntity<String> status(@PathVariable String sku) {
        // Instance details that handled the response
        return ResponseEntity.ok("Responsed by : " + instanceId + ". For request on: " + sku);
    }
}
