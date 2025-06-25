package com.RateShield.dto;

import java.util.List;

public class TokenRequest {
    public String tier;
    public List<String> scopes;
    public double expiresInDays;
}

