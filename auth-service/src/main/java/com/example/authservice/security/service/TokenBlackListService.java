package com.example.authservice.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class TokenBlackListService {

    private final RedisTemplate<String, String> redisTemplate;
    private final static String PREFIX = "bx:";

    public void blackListToken(String token, long expiryInMillis) throws NoSuchAlgorithmException {
        String key = PREFIX.concat(TokenHasher.hashToken(token));
        redisTemplate
                .opsForValue()
                .set(key, "revoked", Duration.ofMillis(expiryInMillis));
    }

    public boolean isTokenBlackListed(String token) throws NoSuchAlgorithmException {
        return redisTemplate.hasKey(PREFIX.concat(TokenHasher.hashToken(token)));
    }

    private static class TokenHasher {
        public static String hashToken(String token) throws NoSuchAlgorithmException {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        }
    }
}
