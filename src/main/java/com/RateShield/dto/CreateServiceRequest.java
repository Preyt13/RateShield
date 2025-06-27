package com.RateShield.dto;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateServiceRequest {
    private String name;
    private String baseUrl;
    private UUID envId;
    private UUID orgId;
    private String rateLimitTier; // optional
}

