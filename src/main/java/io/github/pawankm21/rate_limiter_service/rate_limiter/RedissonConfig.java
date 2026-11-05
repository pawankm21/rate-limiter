package io.github.pawankm21.rate_limiter_service.rate_limiter;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Creates the {@link RedissonClient} used by the Redisson-backed token bucket.
 *
 * <p>Only active when {@code rate-limiter.algorithm=redisson}. The connection reuses the
 * {@code spring.data.redis.*} properties so both Redis clients point at the same server.
 */
@Configuration
@ConditionalOnProperty(name = "rate-limiter.algorithm", havingValue = "redisson")
public class RedissonConfig {

    @Value("${spring.data.redis.host:localhost}")
    private String host;

    @Value("${spring.data.redis.port:6379}")
    private int port;

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(RedissonClient.class)
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port);
        return Redisson.create(config);
    }
}
