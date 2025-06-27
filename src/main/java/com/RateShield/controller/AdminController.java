package com.RateShield.controller;

import com.RateShield.dto.LoginRequest;
import com.RateShield.dto.LoginResponse;
import com.RateShield.dto.TokenRequest;
import com.RateShield.dto.TokenMetadata;
import com.RateShield.model.ApiToken;
import com.RateShield.model.User;
import com.RateShield.service.UserService;
import com.RateShield.util.JwtUtil;
import com.RateShield.repository.ApiTokenRepository;

import io.jsonwebtoken.Claims;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final ApiTokenRepository apiTokenRepo;

    public AdminController(UserService userService, JwtUtil jwtUtil, ApiTokenRepository apiTokenRepo) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.apiTokenRepo = apiTokenRepo;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = userService.login(request.username, request.password);
        if (user == null || !user.isAdmin()) {
            return ResponseEntity.status(403).body("Unauthorized");
        }

        String token = jwtUtil.generateToken(
            user.getUsername(),
            user.getTier(),
            user.getOrganization().getId(),
            "ADMIN"
        );

        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/tokens")
    public ResponseEntity<?> issueToken(
        @RequestBody TokenRequest dto,
        @RequestHeader("Authorization") String authHeader
    ) {
        String jwt = authHeader.replace("Bearer ", "");
        Claims claims = jwtUtil.extractAllClaims(jwt);

        if (!"ADMIN".equals(claims.get("role", String.class))) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        UUID orgId = UUID.fromString(claims.get("orgId", String.class));
        String generatedToken = UUID.randomUUID().toString();

        ApiToken token = new ApiToken();
        token.setToken(generatedToken);
        token.setOrgId(orgId);
        token.setTier(dto.tier);

        // Clean scopes
        String cleanedScopes = dto.scopes.stream()
            .map(String::trim)
            .map(path -> path.replaceAll("/+$", ""))
            .collect(Collectors.joining(","));
        token.setScopes(cleanedScopes);

        token.setRevoked(false);
        token.setCreatedAt(LocalDateTime.now());

        long secondsToExpiry = (long) (dto.expiresInDays * 24 * 60 * 60);
        token.setExpiresAt(LocalDateTime.now().plusSeconds(secondsToExpiry));

        apiTokenRepo.save(token);

        return ResponseEntity.ok(
            new TokenMetadata(
                token.getId(),
                token.getToken(),
                token.getTier(),
                token.getScopes(),
                token.getCreatedAt(),
                token.getExpiresAt(),
                token.getOrgId(),
                token.isRevoked()
            )
        );
    }

    @GetMapping("/tokens/{id}")
    public ResponseEntity<?> getTokenMetadata(
        @PathVariable UUID id,
        @RequestHeader("Authorization") String authHeader
    ) {
        String jwt = authHeader.replace("Bearer ", "");
        Claims claims = jwtUtil.extractAllClaims(jwt);

        if (!"ADMIN".equals(claims.get("role", String.class))) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        UUID adminOrgId = UUID.fromString(claims.get("orgId", String.class));

        return apiTokenRepo.findById(id)
            .map(token -> {
                if (!token.getOrgId().equals(adminOrgId)) {
                    return ResponseEntity.status(403).body("Access denied");
                }

                return ResponseEntity.ok(new TokenMetadata(
                    token.getId(),
                    token.getToken(),
                    token.getTier(),
                    token.getScopes(),
                    token.getCreatedAt(),
                    token.getExpiresAt(),
                    token.getOrgId(),
                    token.isRevoked()
                ));
            })
            .orElse(ResponseEntity.status(404).body("Token not found"));
    }

    @GetMapping("/tokens")
    public ResponseEntity<?> getAllTokensForOrg(@RequestHeader("Authorization") String authHeader) {
        String jwt = authHeader.replace("Bearer ", "");
        Claims claims = jwtUtil.extractAllClaims(jwt);

        if (!"ADMIN".equals(claims.get("role", String.class))) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        UUID orgId = UUID.fromString(claims.get("orgId", String.class));

        List<TokenMetadata> tokens = apiTokenRepo.findByOrgId(orgId).stream()
            .map(token -> new TokenMetadata(
                token.getId(),
                token.getToken(),
                token.getTier(),
                token.getScopes(),
                token.getCreatedAt(),
                token.getExpiresAt(),
                token.getOrgId(),
                token.isRevoked()
            ))
            .collect(Collectors.toList());

        return ResponseEntity.ok(tokens);
    }
}
