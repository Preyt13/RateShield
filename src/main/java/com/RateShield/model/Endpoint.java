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
    private String path;

    @Column(nullable = false)
    private String method;

    private String groupLabel;

    private String rateLimitTier;

    private String scopeKey;

    @Column(nullable = false)
    private Instant createdAt;
}
