package com.example.authservice;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.security.NoSuchAlgorithmException;

@SpringBootApplication
@EnableDiscoveryClient
public class AuthServiceApplication {

    public static void main(String[] args) throws NoSuchAlgorithmException {
        // 512 BITS = 64 BYTES
        // openssl rand -base64 64
//        KeyGenerator hmacSHA256 = KeyGenerator.getInstance("HmacSHA512");
//        hmacSHA256.init(512);
//        SecretKey secretKey = hmacSHA256.generateKey();
//        String key = Base64.getEncoder().encodeToString(secretKey.getEncoded());
//        System.out.println(key);

        SpringApplication.run(AuthServiceApplication.class, args);
    }

}
