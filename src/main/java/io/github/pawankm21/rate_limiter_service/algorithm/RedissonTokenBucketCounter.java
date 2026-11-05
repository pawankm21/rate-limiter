package io.github.pawankm21.rate_limiter_service.algorithm;

import io.github.pawankm21.rate_limiter_service.entity.Policy;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.LongCodec;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * Distributed token bucket backed by Redisson.
 *
 * <p>Functionally equivalent to {@link RedisTokenBucketCounter} but uses Redisson's
 * {@link RScript} instead of Spring Data Redis. Redisson resolves a single Redis
 * connection per script evaluation and reuses the script SHA once loaded, which avoids
 * resending the script body on every request.
 *
 * <p>Enable with {@code rate-limiter.algorithm=redisson}.
 */
@Component
@ConditionalOnProperty(name = "rate-limiter.algorithm", havingValue = "redisson")
public class RedissonTokenBucketCounter implements RateLimitAlgorithm {

    private static final String TOKENS_KEY = "rate-limiter:bucket:{%d}:tokens";
    private static final String TIMESTAMP_KEY = "rate-limiter:bucket:{%d}:timestamp";

    private final RScript script;
    private final String scriptSource;

    public RedissonTokenBucketCounter(RedissonClient redissonClient) {
        this.script = redissonClient.getScript(LongCodec.INSTANCE);
        this.scriptSource = readScript();
    }

    private static String readScript() {
        try (var in = new ClassPathResource("scripts/token_bucket.lua").getInputStream()) {
            return StreamUtils.copyToString(in, StandardCharsets.UTF_8);
        }
        catch (IOException ex) {
            throw new UncheckedIOException("Unable to load scripts/token_bucket.lua", ex);
        }
    }

    @Override
    public boolean isRequestAllowed(long clientId, Date timeStamp, Policy policy) {
        long ratePerSecond = Math.max(1L, policy.getMaxRequests() / Math.max(1L, policy.getTimeWindow()));
        long capacity = Math.max(1L, policy.getMaxRequests());
        long nowSeconds = timeStamp.toInstant().getEpochSecond();

        List<Long> result = script.eval(
                RScript.Mode.READ_WRITE,
                scriptSource,
                RScript.ReturnType.MULTI,
                List.of(String.format(TOKENS_KEY, clientId), String.format(TIMESTAMP_KEY, clientId)),
                ratePerSecond, capacity, nowSeconds, 1L);

        return result != null && !result.isEmpty() && result.getFirst() == 1L;
    }
}
