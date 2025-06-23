package com.RateShield.ratelimiter.core;

public class TokenBucket {
    private final int capacity;
    private final int refillRatePerSecond;
    private int tokens;
    private long lastRefillTimestamp;

    public TokenBucket(int capacity, int refillRatePerSecond) {
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
        this.tokens = capacity;
        this.lastRefillTimestamp = System.nanoTime();
    }

    public synchronized boolean allowRequest() {
        refill();
        if (tokens > 0) {
            tokens--;
            return true;
        }
        return false;
    }

    private void refill() {
        long now = System.nanoTime();
        long elapsedSeconds = (now - lastRefillTimestamp) / 1_000_000_000;
        if (elapsedSeconds > 0) {
            int tokensToAdd = (int) (elapsedSeconds * refillRatePerSecond);
            tokens = Math.min(capacity, tokens + tokensToAdd);
            lastRefillTimestamp = now;
        }
    }
}
