package com.RateShield.repository;

import com.RateShield.model.RegisteredService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RegisteredServiceRepository extends JpaRepository<RegisteredService, UUID> {

    List<RegisteredService> findByOrgId(UUID orgId);

    List<RegisteredService> findByEnvId(UUID envId);

    boolean existsByNameAndEnvId(String name, UUID envId ); // Optional uniqueness check
}

