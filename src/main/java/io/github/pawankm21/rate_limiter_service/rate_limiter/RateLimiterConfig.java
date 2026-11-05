package io.github.pawankm21.rate_limiter_service.rate_limiter;

import java.net.InetSocketAddress;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;

import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {

    private static final String CLIENT_ID_HEADER = "X-Client-Id";

    @Bean
    @Primary
    public KeyResolver clientKeyResolver() {
        return exchange -> {
            String clientId = exchange.getRequest().getHeaders().getFirst(CLIENT_ID_HEADER);
            if (StringUtils.hasText(clientId)) {
                return Mono.just("client:" + clientId);
            }

            InetSocketAddress remoteAddress = exchange.getRequest().getRemoteAddress();
            String ipAddress = remoteAddress == null
                    ? "unknown"
                    : remoteAddress.getAddress().getHostAddress();
            return Mono.just("ip:" + ipAddress);
        };
    }

    /**
     * Resolves the raw {@code X-Client-Id} header without a prefix.
     *
     * <p>The custom {@link CustomRateLimiter} parses the key as a client id to look up
     * that client's policy, so the key must be the bare numeric id rather than the
     * {@code client:} prefixed value produced by {@link #clientKeyResolver()}.
     */
    @Bean
    public KeyResolver numericClientKeyResolver() {
        return exchange -> {
            String clientId = exchange.getRequest().getHeaders().getFirst(CLIENT_ID_HEADER);
            if (StringUtils.hasText(clientId)) {
                return Mono.just(clientId);
            }

            InetSocketAddress remoteAddress = exchange.getRequest().getRemoteAddress();
            String ipAddress = remoteAddress == null
                    ? "unknown"
                    : remoteAddress.getAddress().getHostAddress();
            return Mono.just("ip:" + ipAddress);
        };
    }
}
