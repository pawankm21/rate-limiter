package io.github.pawankm21.rate_limiter_service.algorithm;

import io.github.pawankm21.rate_limiter_service.entity.Policy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Distributed token bucket backed by Spring Data Redis.
 *
 * <p>The whole refill/consume step runs inside the {@code token_bucket.lua} script so that
 * the read-modify-write cycle is atomic on the Redis server. This is what makes the
 * algorithm safe across multiple gateway instances.
 */
@Component
@ConditionalOnProperty(name = "rate-limiter.algorithm", havingValue = "redis", matchIfMissing = true)
public class RedisTokenBucketCounter implements RateLimitAlgorithm {

    // Hash tags keep both keys of one bucket on the same cluster slot.
    static final String TOKENS_KEY = "rate-limiter:bucket:{%d}:tokens";
    static final String TIMESTAMP_KEY = "rate-limiter:bucket:{%d}:timestamp";

    private final StringRedisTemplate redisTemplate;

    private final RedisScript<List> script;

    public RedisTokenBucketCounter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        DefaultRedisScript<List> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("scripts/token_bucket.lua")));
        redisScript.setResultType(List.class);
        this.script = redisScript;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean isRequestAllowed(long clientId, Date timeStamp, Policy policy) {
        long ratePerSecond = Math.max(1L, policy.getMaxRequests() / Math.max(1L, policy.getTimeWindow()));
        long capacity = Math.max(1L, policy.getMaxRequests());
        long nowSeconds = timeStamp.toInstant().getEpochSecond();

        List<String> keys = List.of(
                String.format(TOKENS_KEY, clientId),
                String.format(TIMESTAMP_KEY, clientId));

        List<Number> result = (List<Number>) redisTemplate.execute(
                script, keys,
                String.valueOf(ratePerSecond),
                String.valueOf(capacity),
                String.valueOf(nowSeconds),
                String.valueOf(1L));

        return result != null && !result.isEmpty() && result.getFirst().longValue() == 1L;
    }
}
