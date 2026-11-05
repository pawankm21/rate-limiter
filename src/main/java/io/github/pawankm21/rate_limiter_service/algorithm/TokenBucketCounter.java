package io.github.pawankm21.rate_limiter_service.algorithm;

import io.github.pawankm21.rate_limiter_service.dto.BucketState;
import io.github.pawankm21.rate_limiter_service.entity.Policy;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class TokenBucketCounter implements RateLimitAlgorithm {
    private final long defaultBurstCapacity;
    private final ConcurrentMap<Long, BucketState> buckets;

    public TokenBucketCounter() {
        this(10L);
    }

    public TokenBucketCounter(long defaultBurstCapacity) {
        this.buckets = new ConcurrentHashMap<>();
        this.defaultBurstCapacity = defaultBurstCapacity;
    }

    @Override
    public boolean isRequestAllowed(long clientId, Date timeStamp, Policy policy) {
        double refillRatePerMs = (double) policy.getMaxRequests() / (policy.getTimeWindow() * 1000.0);
        long currentTimeMillis = timeStamp.toInstant().toEpochMilli();
        long capacity = Math.min(defaultBurstCapacity, policy.getMaxRequests());

        AtomicBoolean allowed = new AtomicBoolean(false);

        // compute() executes atomically per clientId key
        buckets.compute(clientId, (key, prevState) -> {
            if (prevState == null) {
                if (capacity >= 1) {
                    allowed.set(true);
                    return new BucketState(new Date(currentTimeMillis), capacity - 1.0);
                } else {
                    allowed.set(false);
                    return new BucketState(new Date(currentTimeMillis), 0.0);
                }
            }

            long elapsedMillis = Math.max(0L, currentTimeMillis - prevState.getEpochMillis());
            double refilledTokens = Math.min(
                    capacity,
                    prevState.getTokens() + (elapsedMillis * refillRatePerMs)
            );

            if (refilledTokens < 1.0) {
                allowed.set(false);
                // Keep accumulated fractional tokens and advance timestamp
                return new BucketState(new Date(Math.max(currentTimeMillis, prevState.getEpochMillis())), refilledTokens);
            } else {
                allowed.set(true);
                return new BucketState(new Date(currentTimeMillis), refilledTokens - 1.0);
            }
        });

        return allowed.get();
    }
}