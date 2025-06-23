package com.RateShield.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private Key secretKey;
    private final long EXPIRATION_TIME = 1000 * 60 * 15; // 15 mins

    @PostConstruct
    public void init() {
        // Hardcoded secret for now (in real app use env var or config)
        this.secretKey = Keys.hmacShaKeyFor("very-strong-and-secure-secret-key-1234567890".getBytes());
    }

    public String generateToken(String username, String tier) {
        return Jwts.builder()
                .setSubject(username)
                .claim("tier", tier)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractTier(String token) {
        return extractAllClaims(token).get("tier", String.class);
    }
}

