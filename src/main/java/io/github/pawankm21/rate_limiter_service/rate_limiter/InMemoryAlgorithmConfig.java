package io.github.pawankm21.rate_limiter_service.rate_limiter;

import io.github.pawankm21.rate_limiter_service.algorithm.RateLimitAlgorithm;
import io.github.pawankm21.rate_limiter_service.algorithm.TokenBucketCounter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Selects the in-memory token bucket.
 *
 * <p>The three implementations are mutually exclusive, driven by
 * {@code rate-limiter.algorithm}:
 * <ul>
 *     <li>{@code redis} (default) - {@code RedisTokenBucketCounter}</li>
 *     <li>{@code redisson} - {@code RedissonTokenBucketCounter}</li>
 *     <li>{@code in-memory} - {@code TokenBucketCounter}, single instance only</li>
 * </ul>
 */
@Configuration
@ConditionalOnProperty(name = "rate-limiter.algorithm", havingValue = "in-memory")
public class InMemoryAlgorithmConfig {

    @Bean
    public RateLimitAlgorithm inMemoryTokenBucketCounter() {
        return new TokenBucketCounter();
    }
}
