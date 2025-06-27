package com.RateShield.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;


import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    private Key secretKey;
    private static final long EXPIRATION_TIME = 1000 * 60 * 15; // 15 mins

    @Value("${TOKEN_KEY}")
    private String tokenKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(tokenKey.getBytes());
    }

    /**
     * Generate a signed JWT with custom claims.
     */

    public String generateToken(String username, String tier, UUID orgId, String role) {
    return Jwts.builder()
        .setSubject(username)
        .claim("tier", tier)
        .claim("orgId", orgId)
        .claim("role", role)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .signWith(secretKey, SignatureAlgorithm.HS256)
        .compact();
    }

    /**
     * Validate token signature and expiration.
     */

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Extract all JWT claims from a token.
     */

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

    public UUID extractOrgId(String token) {
        String raw = extractAllClaims(token).get("orgId", String.class);
        return UUID.fromString(raw); // convert properly here
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }
}

