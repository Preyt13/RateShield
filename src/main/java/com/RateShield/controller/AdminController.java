package com.RateShield.controller;

import com.RateShield.dto.LoginRequest;
import com.RateShield.dto.LoginResponse;
import com.RateShield.dto.TokenRequest;
import com.RateShield.dto.TokenMetadata;
import com.RateShield.model.User;
import com.RateShield.service.UserService;
import com.RateShield.service.TokenService;
import com.RateShield.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    public AdminController(UserService userService, JwtUtil jwtUtil, TokenService tokenService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.tokenService = tokenService;
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
        TokenMetadata metadata = tokenService.issueScopedToken(dto, orgId);
        return ResponseEntity.ok(metadata);
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

        UUID orgId = UUID.fromString(claims.get("orgId", String.class));
        return tokenService.getTokenMetadataById(id, orgId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
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
        List<TokenMetadata> tokens = tokenService.getAllTokensForOrg(orgId);
        return ResponseEntity.ok(tokens);
    }
}
