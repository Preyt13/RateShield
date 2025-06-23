package com.RateShield.auth;

public class User {
    private String username;
    private String password;
    private String tier; // e.g., FREE, PRO, ENTERPRISE

    public User(String username, String password, String tier) {
        this.username = username;
        this.password = password;
        this.tier = tier;
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

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }
} 

