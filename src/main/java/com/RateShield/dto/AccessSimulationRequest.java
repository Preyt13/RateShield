package com.RateShield.dto;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessSimulationRequest {
    private UUID orgId;
    private UUID serviceId;        
    private String endpointPath;    
    private String method;          
    private String token;           // JWT or UUID token
}
