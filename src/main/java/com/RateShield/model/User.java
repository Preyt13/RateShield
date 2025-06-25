package com.RateShield.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    private String tier; // Feature decision: organisation based tiers cusstom tier based on env and resoruce, customisable in admin console

    private boolean isAdmin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", nullable = false)
    private Organization organization;

    public User() {}

    public User(String username, String password, String tier, boolean isAdmin, Organization organization) {
        this.username = username;
        this.password = password;
        this.tier = tier;
        this.isAdmin = isAdmin;
        this.organization = organization;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getTier() {
        return tier;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
}
