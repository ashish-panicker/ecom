package com.example.authservice.security.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Service
public class JwtService {

    private final String JWT_SECRET;
    private final long JWT_TOKEN_EXPIRATION;
    private final long REFRESH_TOKEN_EXPIRATION;
    private final SecretKey secretKey;

    public JwtService() {
        this.JWT_SECRET = "RvEGc4mwJvw5R+Tx1+ZMEkQPVPMuIjMcpWG2YyHfhyqdMKIa53Jhs8iBbRaXqJ2Ug6AgN0afcK1fmb2Y8l2A+w==";
        this.JWT_TOKEN_EXPIRATION = 3600000;
        this.secretKey = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
        this.REFRESH_TOKEN_EXPIRATION = 86400000;
    }

    public String generateRefreshToken(String userName) {
        return Jwts.builder()
                .subject(userName)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public String generateToken(String userName, List<String> roles) {
        return Jwts.builder()
                .subject(userName)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + JWT_TOKEN_EXPIRATION))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public String generateToken(Authentication auth) {
        return Jwts.builder()
                .subject(auth.getName())
                .claim("roles", auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + JWT_TOKEN_EXPIRATION))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public String extractUserName(String token) {
        return extractClaims(token, Claims::getSubject);

    }

    public List<String> extractRoles(String token) {
        var claims = extractAllClaims(token);
        return claims.get("roles", List.class);
    }

    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private <T> T extractClaims(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(secretKey)
                .build().parseSignedClaims(token)
                .getPayload();
    }

}
