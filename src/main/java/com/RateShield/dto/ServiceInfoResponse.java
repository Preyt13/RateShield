package com.RateShield.dto;

import lombok.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceInfoResponse {
    private UUID id;
    private String name;
    private String baseUrl;
    private String rateLimitTier;
}

