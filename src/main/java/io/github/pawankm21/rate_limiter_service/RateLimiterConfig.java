package io.github.pawankm21.rate_limiter_service;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.Principal;

@Configuration
public class RateLimiterConfig {

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> exchange.getPrincipal().map(Principal::getName);
    }
}
