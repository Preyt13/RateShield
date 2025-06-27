package com.RateShield.ratelimiter.service;

import com.RateShield.model.Environment;
import com.RateShield.model.Organization;
import com.RateShield.model.RegisteredService;
import com.RateShield.model.Endpoint;
import com.RateShield.ratelimiter.core.TokenBucket;
import com.RateShield.config.PlanRateConfig;
import com.RateShield.repository.EnvironmentRepository;
import com.RateShield.repository.RegisteredServiceRepository;
import com.RateShield.repository.EndpointRepository;
import com.RateShield.service.OrganizationService;
import com.RateShield.util.RateLimitResolverUtil;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class RateLimiterServiceV2 {

    private final ConcurrentMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    private final OrganizationService organizationService;
    private final RegisteredServiceRepository serviceRepo;
    private final EndpointRepository endpointRepo;
    private final EnvironmentRepository envRepo;
    private final RateLimitResolverUtil resolverUtil;

    public RateLimiterServiceV2(
            OrganizationService organizationService,
            RegisteredServiceRepository serviceRepo,
            EndpointRepository endpointRepo,
            EnvironmentRepository envRepo,
            RateLimitResolverUtil resolverUtil
    ) {
        this.organizationService = organizationService;
        this.serviceRepo = serviceRepo;
        this.endpointRepo = endpointRepo;
        this.envRepo = envRepo;
        this.resolverUtil = resolverUtil;
    }

    public boolean isAllowed(UUID orgId, UUID envId, UUID serviceId, String path, String method, String tokenId) {

        // Fetch all entities (nullable is okay)
        Optional<Endpoint> endpoint = endpointRepo.findByServiceIdAndPathAndMethod(serviceId, path, method);
        Optional<RegisteredService> service = serviceRepo.findById(serviceId);
        Optional<Environment> environment = envRepo.findById(envId);
        Organization org = organizationService.findById(orgId);

        // Resolve effective rate limit
        PlanRateConfig.RateLimitParams tier = resolverUtil.resolveRateLimit(
                endpoint.orElse(null),
                service.orElse(null),
                environment.orElse(null),
                org
        );

        // Create composite key per token + endpoint
        String key = orgId + ":" + tokenId + ":" + serviceId + ":" + path + ":" + method;

        TokenBucket bucket = buckets.computeIfAbsent(key,
                k -> new TokenBucket(tier.capacity, tier.refillRate));

        return bucket.allowRequest();
    }

        // Fallback method used by interceptors when full path/env/service is unavailable
    public boolean isAllowed(UUID orgId, String tokenId) {
        Organization org = organizationService.findById(orgId);

        PlanRateConfig.RateLimitParams tier = resolverUtil.resolveRateLimit(
                null, null, null, org
        );

        String key = orgId + ":" + tokenId;
        TokenBucket bucket = buckets.computeIfAbsent(key,
                k -> new TokenBucket(tier.capacity, tier.refillRate));

        return bucket.allowRequest();
    }

}
