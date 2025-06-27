package com.RateShield.service.impl;

import com.RateShield.model.Environment;
import com.RateShield.repository.EnvironmentRepository;
import com.RateShield.service.EnvironmentService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class EnvironmentServiceImpl implements EnvironmentService {

    private final EnvironmentRepository envRepo;

    public EnvironmentServiceImpl(EnvironmentRepository envRepo) {
        this.envRepo = envRepo;
    }

    @Override
    public Environment createEnvironment(String name, boolean isBaseEnv, UUID orgId, UUID createdBy) {
        Environment env = Environment.builder()
                .name(name)
                .isBaseEnv(isBaseEnv)
                .orgId(orgId)
                .createdBy(createdBy)
                .createdAt(Instant.now())
                .build();
        return envRepo.save(env);
    }

    @Override
    public List<Environment> getEnvironmentsByOrg(UUID orgId) {
        return envRepo.findByOrgId(orgId);
    }

    @Override
    public Environment getBaseEnvironmentForOrg(UUID orgId) {
        return envRepo.findByOrgId(orgId).stream()
                .filter(Environment::isBaseEnv)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Base environment not found for org " + orgId));
    }
}
