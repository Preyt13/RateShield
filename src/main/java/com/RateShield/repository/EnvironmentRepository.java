package com.RateShield.repository;

import com.RateShield.model.Environment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EnvironmentRepository extends JpaRepository<Environment, UUID> {

    List<Environment> findByOrgId(Long orgId);

    List<Environment> findByOrgIdAndIsBaseEnv(Long orgId, boolean isBaseEnv);
}

