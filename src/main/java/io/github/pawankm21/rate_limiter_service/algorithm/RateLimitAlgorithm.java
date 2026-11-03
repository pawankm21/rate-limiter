package io.github.pawankm21.rate_limiter_service.algorithm;

import io.github.pawankm21.rate_limiter_service.entity.Policy;

import java.util.Date;

public interface RateLimitAlgorithm {
    public boolean isRequestAllowed(long clientId, Date timeStamp, Policy policy);
}
