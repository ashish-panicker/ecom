package com.example.authservice.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenBlackListService {

    private final RedisTemplate<String, String> redisTemplate;
    private final static String PREFIX = "bx:";

    public void blackListToken(String token, long expiryInMillis) {
        String key = PREFIX.concat(token);
        redisTemplate
                .opsForValue()
                .set(key, "revoked", Duration.ofMillis(expiryInMillis));
    }

    public boolean isTokenBlackListed(String token) {
        return redisTemplate.hasKey(PREFIX.concat(token));
    }
}
