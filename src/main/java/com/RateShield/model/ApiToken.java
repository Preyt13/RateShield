package com.RateShield.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class ApiToken {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private UUID orgId;

    private String tier;

    private String scopes;

    private boolean revoked;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    // Getters
    public UUID getId() { return id; }
    public String getToken() { return token; }
    public UUID getOrgId() { return orgId; }
    public String getTier() { return tier; }
    public String getScopes() { return scopes; }
    public boolean isRevoked() { return revoked; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }

    // Setters
    public void setId(UUID id) { this.id = id; }
    public void setToken(String token) { this.token = token; }
    public void setOrgId(UUID orgId) { this.orgId = orgId; }
    public void setTier(String tier) { this.tier = tier; }
    public void setScopes(String scopes) { this.scopes = scopes; }
    public void setRevoked(boolean revoked) { this.revoked = revoked; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}
