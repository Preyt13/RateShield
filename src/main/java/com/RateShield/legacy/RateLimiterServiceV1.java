// Note: This legacy IP-based limiter will be deprecated in v1.1+
// It is preserved here for compatibility/debug purposes only.
// Future versions will use DB-backed tier + endpoint-based limits.

package com.RateShield.legacy;

import com.RateShield.ratelimiter.core.TokenBucket;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class RateLimiterServiceV1 {

    private final ConcurrentMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    private static final int CAPACITY = 3;
    private static final int REFILL_RATE = 1;

    public boolean isAllowed(String clientIp) {
        TokenBucket bucket = buckets.computeIfAbsent(clientIp,
            key -> new TokenBucket(CAPACITY, REFILL_RATE));

        return bucket.allowRequest();
    }
}
