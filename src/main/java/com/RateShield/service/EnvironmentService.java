package com.RateShield.service;
import com.RateShield.model.Environment;

import java.util.List;
import java.util.UUID;

public interface EnvironmentService {
    Environment createEnvironment(String name, boolean isBaseEnv, UUID orgId, UUID createdBy);

    List<Environment> getEnvironmentsByOrg(UUID orgId);

    Environment getBaseEnvironmentForOrg(UUID orgId);
}

