package com.RateShield.config;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class PlanRateConfig {

    private final Map<String, RateLimitParams> config = new HashMap<>();

    public PlanRateConfig() {
        config.put("FREE", new RateLimitParams(2, 1));
        config.put("PRO", new RateLimitParams(10, 5));
        config.put("ENTERPRISE", new RateLimitParams(20, 10));
    }

    public RateLimitParams getRateLimitForPlan(String plan) {
        return config.getOrDefault(plan.toUpperCase(), new RateLimitParams(1, 1));
    }

    public static class RateLimitParams {
        public final int capacity;
        public final int refillRate;

        public RateLimitParams(int capacity, int refillRate) {
            this.capacity = capacity;
            this.refillRate = refillRate;
        }
    }
}