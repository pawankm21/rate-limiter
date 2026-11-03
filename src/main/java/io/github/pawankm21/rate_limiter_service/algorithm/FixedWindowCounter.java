package io.github.pawankm21.rate_limiter_service.algorithm;

import io.github.pawankm21.rate_limiter_service.entity.Policy;
import io.github.pawankm21.rate_limiter_service.repository.PolicyRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FixedWindowCounter implements RateLimitAlgorithm {
    private final Map<String, List<Long>> counter;
    private PolicyRepository policyRepository;

    public String getKey(long clientId) {
        return String.join(":", "rate-limiter", String.valueOf(clientId));

    }

    private long getWindowId(Date timeStamp, long windowLength) {
        long currentSecond = timeStamp.toInstant().getEpochSecond();
        return currentSecond / windowLength;
    }

    @Override
    public boolean isRequestAllowed(long clientId, Date timeStamp, Policy policy) {
        String key = getKey(clientId);
        long windowId = getWindowId(timeStamp, policy.getTimeWindow());
        if (counter.containsKey(key)) {
            List<Long> timeStampCount = counter.get(key);
            if (timeStampCount.getFirst() != windowId) {
                timeStampCount.set(0, windowId);
                timeStampCount.set(1, 1L);
                return true;
            } else if (timeStampCount.get(1) < policy.getMaxRequests()) {
                timeStampCount.set(1, timeStampCount.get(1) + 1);
                counter.put(key, timeStampCount);
                return true;
            }

        } else {
            List<Long> timeStampCount = new ArrayList<>(List.of(windowId, 1L));
            counter.put(key, timeStampCount);
            return true;
        }
        return false;
    }

    FixedWindowCounter() {
        counter = new HashMap<>();
    }

}
