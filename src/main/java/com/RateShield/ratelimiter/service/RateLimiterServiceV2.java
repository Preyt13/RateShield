package com.RateShield.ratelimiter.service;

import com.RateShield.ratelimiter.core.TokenBucket;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class RateLimiterServiceV2 {

    private final ConcurrentMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    public boolean isAllowed(String userId, String tier) {
        // Adjust CAPACITY and REFILL_RATE based on tier
        int capacity, refillRate;

        switch (tier.toUpperCase()) {
            case "PRO":
                capacity = 10;
                refillRate = 5;
                break;
            case "ENTERPRISE":
                capacity = 20;
                refillRate = 10;
                break;
            default:
                capacity = 3;
                refillRate = 1;
                break;
        }

        TokenBucket bucket = buckets.computeIfAbsent(userId,
            key -> new TokenBucket(capacity, refillRate));

        return bucket.allowRequest();
    }
}