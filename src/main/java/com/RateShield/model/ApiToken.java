package com.RateShield.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ApiToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    private Long orgId;

    private String tier;

    private String scopes;

    private boolean revoked;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;


    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public Long getOrgId() {
        return orgId;
    }

    public String getTier() {
        return tier;
    }

    public String getScopes() {
        return scopes;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public void setScopes(String scopes) {
        this.scopes = scopes;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
