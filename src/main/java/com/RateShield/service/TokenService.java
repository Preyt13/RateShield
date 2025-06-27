package com.RateShield.service;

import com.RateShield.dto.TokenMetadata;
import com.RateShield.dto.TokenRequest;
import com.RateShield.model.ApiToken;
import io.jsonwebtoken.Claims;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TokenService {
    String generateJwtToken(String username, String tier, UUID orgId, String role);
    boolean validateJwtToken(String jwt);
    Claims extractClaims(String jwt);

    TokenMetadata issueScopedToken(TokenRequest dto, UUID orgId);
    Optional<ApiToken> getValidApiToken(String token);
    List<TokenMetadata> getAllTokensForOrg(UUID orgId);
    Optional<TokenMetadata> getTokenMetadataById(UUID tokenId, UUID orgId);
}
