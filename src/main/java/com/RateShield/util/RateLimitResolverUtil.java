package com.RateShield.util;

import com.RateShield.config.PlanRateConfig;
import com.RateShield.config.PlanRateConfig.RateLimitParams;
import com.RateShield.model.Endpoint;
import com.RateShield.model.Environment;
import com.RateShield.model.Organization;
import com.RateShield.model.RegisteredService;

import org.springframework.stereotype.Component;

@Component
public class RateLimitResolverUtil {

    private final PlanRateConfig planRateConfig;

    public RateLimitResolverUtil(PlanRateConfig planRateConfig) {
        this.planRateConfig = planRateConfig;
    }

    public RateLimitParams resolveRateLimit(
            Endpoint endpoint,
            RegisteredService service,
            Environment env,
            Organization org
    ) {
        if (endpoint != null && endpoint.getRateLimitTier() != null) {
            return planRateConfig.getRateLimitForPlan(endpoint.getRateLimitTier());
        }

        if (service != null && service.getRateLimitTier() != null) {
            return planRateConfig.getRateLimitForPlan(service.getRateLimitTier());
        }

        if (env != null && env.getRateLimitTier() != null) {
            return planRateConfig.getRateLimitForPlan(env.getRateLimitTier());
        }

        return planRateConfig.getRateLimitForPlan(org.getPlan());
    }
}
