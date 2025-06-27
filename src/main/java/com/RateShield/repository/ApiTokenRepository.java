package com.RateShield.repository;

import com.RateShield.model.ApiToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApiTokenRepository extends JpaRepository<ApiToken, UUID> {

    Optional<ApiToken> findByToken(String token);

    List<ApiToken> findByOrgId(UUID orgId);
}
