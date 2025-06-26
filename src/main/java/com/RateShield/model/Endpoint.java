package com.RateShield.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "endpoints")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Endpoint {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID serviceId;

    @Column(nullable = false)
    private String path; // e.g. "/api/ping"

    @Column(nullable = false)
    private String method; // GET, POST, etc.

    private String groupLabel; // e.g. "api"

    private String rateLimitTier; // Optional override

    private String scopeKey; // e.g. "SERVICE.PING.READ"

    @Column(nullable = false)
    private Instant createdAt;
}

