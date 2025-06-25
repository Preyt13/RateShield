package com.RateShield.dto;

import java.time.LocalDateTime;

public class TokenMetadata {

    public Long id;
    public String token;
    public String tier;
    public String scopes;
    public LocalDateTime createdAt;
    public LocalDateTime expiresAt;
    public Long orgId;
    public boolean revoked;

    public TokenMetadata(Long id, String token, String tier, String scopes,
                            LocalDateTime createdAt, LocalDateTime expiresAt,
                            Long orgId, boolean revoked) {
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
