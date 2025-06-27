package com.RateShield.ratelimiter.service;

import com.RateShield.model.Organization;
import com.RateShield.ratelimiter.core.TokenBucket;
import com.RateShield.service.OrganizationService;
import com.RateShield.config.PlanRateConfig;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.UUID;

@Service
public class RateLimiterServiceV2 {

    private final ConcurrentMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();
    private final OrganizationService organizationService;
    private final PlanRateConfig planRateConfig;

    public RateLimiterServiceV2(OrganizationService organizationService, PlanRateConfig planRateConfig) {
        this.organizationService = organizationService;
        this.planRateConfig = planRateConfig;
    }

    public boolean isAllowed(UUID orgId, String tokenId) {
        Organization org = organizationService.findById(orgId);
        String plan = org.getPlan();

        PlanRateConfig.RateLimitParams params = planRateConfig.getRateLimitForPlan(plan);

        String bucketKey = orgId + ":" + tokenId;
        TokenBucket bucket = buckets.computeIfAbsent(bucketKey,
                key -> new TokenBucket(params.capacity, params.refillRate));

        return bucket.allowRequest();
    }
}
