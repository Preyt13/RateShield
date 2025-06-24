package com.RateShield.controller;

import com.RateShield.model.ApiToken;
import com.RateShield.repository.ApiTokenRepository;
import com.RateShield.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class TokenValidationController {

    private final ApiTokenRepository tokenRepo;
    private final JwtUtil jwtUtil;

    public TokenValidationController(ApiTokenRepository tokenRepo, JwtUtil jwtUtil) {
        this.tokenRepo = tokenRepo;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");

        if (token.split("\\.").length == 3) {
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("Invalid or expired JWT");
            }

            Claims claims = jwtUtil.extractAllClaims(token);
            return ResponseEntity.ok(Map.of(
                "valid", true,
                "user", claims.getSubject(),
                "tier", claims.get("tier", String.class),
                "orgId", claims.get("orgId", Long.class),
                "role", claims.get("role", String.class),
                "expiresAt", claims.getExpiration()
            ));
        } else {
            ApiToken apiToken = tokenRepo.findByToken(token)
                    .filter(t -> !t.isRevoked())
                    .filter(t -> t.getExpiresAt().isAfter(LocalDateTime.now()))
                    .orElse(null);

            if (apiToken == null) {
                return ResponseEntity.status(401).body("Invalid or expired token");
            }

            return ResponseEntity.ok(Map.of(
                "valid", true,
                "tier", apiToken.getTier(),
                "orgId", apiToken.getOrgId(),
                "scopes", apiToken.getScopes().split(","),
                "expiresAt", apiToken.getExpiresAt()
            ));
        }
    }
}
