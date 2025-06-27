package com.RateShield.service.impl;

import com.RateShield.dto.TokenMetadata;
import com.RateShield.dto.TokenRequest;
import com.RateShield.model.ApiToken;
import com.RateShield.repository.ApiTokenRepository;
import com.RateShield.service.TokenService;
import com.RateShield.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TokenServiceImpl implements TokenService {

    private final JwtUtil jwtUtil;
    private final ApiTokenRepository tokenRepo;

    public TokenServiceImpl(JwtUtil jwtUtil, ApiTokenRepository tokenRepo) {
        this.jwtUtil = jwtUtil;
        this.tokenRepo = tokenRepo;
    }

    @Override
    public String generateJwtToken(String username, String tier, UUID orgId, String role) {
        return jwtUtil.generateToken(username, tier, orgId, role);
    }

    @Override
    public boolean validateJwtToken(String jwt) {
        return jwtUtil.validateToken(jwt);
    }

    @Override
    public Claims extractClaims(String jwt) {
        return jwtUtil.extractAllClaims(jwt);
    }

    @Override
    public TokenMetadata issueScopedToken(TokenRequest dto, UUID orgId) {
        String generatedToken = UUID.randomUUID().toString();

        ApiToken token = new ApiToken();
        token.setToken(generatedToken);
        token.setOrgId(orgId);
        token.setTier(dto.tier);

        String cleanedScopes = dto.scopes.stream()
            .map(String::trim)
            .map(path -> path.replaceAll("/+$", ""))
            .collect(Collectors.joining(","));

        token.setScopes(cleanedScopes);
        token.setRevoked(false);
        token.setCreatedAt(LocalDateTime.now());
        long secondsToExpiry = (long) (dto.expiresInDays * 24 * 60 * 60);
        token.setExpiresAt(LocalDateTime.now().plusSeconds(secondsToExpiry));

        tokenRepo.save(token);

        return new TokenMetadata(
            token.getId(),
            token.getToken(),
            token.getTier(),
            token.getScopes(),
            token.getCreatedAt(),
            token.getExpiresAt(),
            token.getOrgId(),
            token.isRevoked()
        );
    }

    @Override
    public Optional<ApiToken> getValidApiToken(String token) {
        return tokenRepo.findByToken(token)
                .filter(t -> !t.isRevoked())
                .filter(t -> t.getExpiresAt().isAfter(LocalDateTime.now()));
    }

    @Override
    public List<TokenMetadata> getAllTokensForOrg(UUID orgId) {
        return tokenRepo.findByOrgId(orgId).stream()
                .map(t -> new TokenMetadata(
                        t.getId(), t.getToken(), t.getTier(),
                        t.getScopes(), t.getCreatedAt(),
                        t.getExpiresAt(), t.getOrgId(), t.isRevoked()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<TokenMetadata> getTokenMetadataById(UUID tokenId, UUID orgId) {
        return tokenRepo.findById(tokenId)
                .filter(t -> t.getOrgId().equals(orgId))
                .map(t -> new TokenMetadata(
                        t.getId(), t.getToken(), t.getTier(),
                        t.getScopes(), t.getCreatedAt(),
                        t.getExpiresAt(), t.getOrgId(), t.isRevoked()));
    }
}
