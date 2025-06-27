package com.RateShield.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "environments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Environment {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)     //Name of the env e.g. PROD DEV UAT
    private String name;

    @Column(nullable = false)     //Variable for the "BASE" Env. All orgs have access to "BASE" env upon registeration..
    private boolean isBaseEnv;

    @Column(nullable = false)
    private UUID orgId;

    @Column(nullable = false)     //Storing user's ID as CreatedBy
    private UUID createdBy;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private String rateLimitTier;
}
