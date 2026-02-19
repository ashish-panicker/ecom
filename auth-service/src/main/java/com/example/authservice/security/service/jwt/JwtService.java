package com.example.authservice.security.service.jwt;

import io.github.cdimascio.dotenv.Dotenv;

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
    private final long EXPIRATION;
    private final SecretKey secretKey;

    public JwtService() {
        Dotenv env = Dotenv.configure().load();
        this.JWT_SECRET = "RvEGc4mwJvw5R+Tx1+ZMEkQPVPMuIjMcpWG2YyHfhyqdMKIa53Jhs8iBbRaXqJ2Ug6AgN0afcK1fmb2Y8l2A+w==";
        this.EXPIRATION = 3600000;
        this.secretKey = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
    }

    public String generateToken(Authentication auth) {
        return Jwts.builder()
                .subject(auth.getName())
                .claim("roles", auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
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
