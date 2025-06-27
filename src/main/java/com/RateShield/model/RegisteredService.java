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

    @Column(nullable = false)    //name of the service e.g. userService
    private String name;

    @Column(nullable = false)   //Base Url for this service e.g. https://api.rateshield.com/
    private String baseUrl;

    @Column(nullable = false)
    private UUID envId;

    @Column(nullable = false)
    private UUID orgId;

    private String rateLimitTier;  //Override mechanism : Rate Limit for specific service

    @Column(nullable = false)
    private Instant createdAt;
}
