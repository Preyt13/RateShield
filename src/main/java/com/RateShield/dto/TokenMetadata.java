package com.RateShield.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class TokenMetadata {

    public UUID id;
    public String token;
    public String tier;
    public String scopes;
    public LocalDateTime createdAt;
    public LocalDateTime expiresAt;
    public UUID orgId;
    public boolean revoked;

    public TokenMetadata(UUID id, String token, String tier, String scopes,
                            LocalDateTime createdAt, LocalDateTime expiresAt,
                            UUID orgId, boolean revoked) {
        this.id = id;
        this.token = token;
        this.tier = tier;
        this.scopes = scopes;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.orgId = orgId;
        this.revoked = revoked;
    }
}
