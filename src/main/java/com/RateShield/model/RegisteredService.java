package com.RateShield.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "services")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisteredService {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name; // e.g. "AuthService"

    @Column(nullable = false)
    private String baseUrl; // e.g. "https://api.rateshield.com"

    @Column(nullable = false)
    private UUID envId;

    @Column(nullable = false)
    private UUID orgId;

    private String rateLimitTier; // Optional override, e.g. "MEDIUM"

    @Column(nullable = false)
    private Instant createdAt;
}

