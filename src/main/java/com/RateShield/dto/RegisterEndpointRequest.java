package com.RateShield.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterEndpointRequest {
    private String path;           // e.g. "/api/ping"
    private String method;         // GET, POST, etc.
    private String groupLabel;     // optional
    private String rateLimitTier;  // optional
    private String scopeKey;       // optional
}

