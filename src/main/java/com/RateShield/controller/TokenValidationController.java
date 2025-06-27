package com.RateShield.controller;

import com.RateShield.model.ApiToken;
import com.RateShield.service.TokenService;
import io.jsonwebtoken.Claims;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class TokenValidationController {

    private final TokenService tokenService;

    public TokenValidationController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");

        // JWT check
        if (token.split("\\.").length == 3) {
            if (!tokenService.validateJwtToken(token)) {
                return ResponseEntity.status(401).body("Invalid or expired JWT");
            }

            Claims claims = tokenService.extractClaims(token);
            return ResponseEntity.ok(Map.of(
                "valid", true,
                "user", claims.getSubject(),
                "tier", claims.get("tier", String.class),
                "orgId", claims.get("orgId", String.class),
                "role", claims.get("role", String.class),
                "expiresAt", claims.getExpiration()
            ));
        }

        // API Token check (UUID format)
            return tokenService.getValidApiToken(token)
            .map(t -> ResponseEntity.ok(Map.of(
                "valid", true,
                "tier", t.getTier(),
                "orgId", t.getOrgId(),
                "scopes", t.getScopes().split(","),
                "expiresAt", t.getExpiresAt()
            )))
            .orElse(ResponseEntity.status(401).body(Map.of(
                "valid", false,
                "error", "Invalid or expired token"
            )));
    }
}
