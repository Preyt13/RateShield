package com.RateShield.controller;

import com.RateShield.dto.LoginRequest;
import com.RateShield.dto.LoginResponse;
import com.RateShield.dto.TokenRequestDTO;
import com.RateShield.model.ApiToken;
import com.RateShield.model.User;
import com.RateShield.service.UserService;
import com.RateShield.util.JwtUtil;
import com.RateShield.repository.ApiTokenRepository;

import io.jsonwebtoken.Claims;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            user.getOrgId(),
            "ADMIN"
        );

        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/tokens")
    public ResponseEntity<?> issueToken(@RequestBody TokenRequestDTO dto, @RequestHeader("Authorization") String authHeader) {
        String jwt = authHeader.replace("Bearer ", "");
        Claims claims = jwtUtil.extractAllClaims(jwt);
    
        if (!"ADMIN".equals(claims.get("role", String.class))) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        Long orgId = claims.get("orgId", Long.class);
        String generatedToken = UUID.randomUUID().toString();

        ApiToken token = new ApiToken();
        token.setToken(generatedToken);
        token.setOrgId(orgId);
        token.setTier(dto.tier);
        token.setScopes(String.join(",", dto.scopes));
        token.setRevoked(false);
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusDays(dto.expiresInDays));

        apiTokenRepo.save(token);

        return ResponseEntity.ok(Map.of(
            "token", generatedToken,
            "expiresAt", token.getExpiresAt()
        ));
    }

}

