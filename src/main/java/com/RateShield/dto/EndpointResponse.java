package com.RateShield.dto;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EndpointResponse {
    private UUID id;
    private String path;
    private String method;
    private String groupLabel;
    private String rateLimitTier;
    private String scopeKey;
}

