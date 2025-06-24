package com.RateShield.dto;

import java.util.List;

public class TokenRequestDTO {
    public String tier;
    public List<String> scopes;
    public int expiresInDays;
}

