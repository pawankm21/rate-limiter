package io.github.pawankm21.rate_limiter_service.algorithm;

import io.github.pawankm21.rate_limiter_service.dto.BucketState;
import io.github.pawankm21.rate_limiter_service.entity.Policy;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TokenBucketCounter implements RateLimitAlgorithm {
    private final long burstCapacity;
    private final Map<Long, BucketState> buckets;

    @Override
    public boolean isRequestAllowed(long clientId, Date timeStamp, Policy policy) {
        double refillRate = policy.getMaxRequests() * 1.0 / (policy.getTimeWindow() * 1000.0);
        long currentTimeStamp = timeStamp.toInstant().toEpochMilli();

        if (buckets.containsKey(clientId)) {
            BucketState prevState = buckets.get(clientId);
            double remainingRequestsTillNow = Math.min(((currentTimeStamp - prevState.getEpochMillis()) * refillRate) + prevState.getTokens(), burstCapacity);

            if (remainingRequestsTillNow < 1) {
                buckets.put(clientId, new BucketState(timeStamp, remainingRequestsTillNow));
                return false;

            } else {
                buckets.put(clientId, new BucketState(timeStamp, remainingRequestsTillNow - 1));
                return true;
            }
        } else {

            buckets.put(clientId, new BucketState(timeStamp, burstCapacity - 1.0));
            return true;
        }

    }

    TokenBucketCounter() {
        buckets = new HashMap<>();
        //from config
        burstCapacity = 10;

    }
}
