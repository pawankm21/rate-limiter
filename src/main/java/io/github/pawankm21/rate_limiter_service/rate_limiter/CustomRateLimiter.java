package io.github.pawankm21.rate_limiter_service.rate_limiter;

import io.github.pawankm21.rate_limiter_service.algorithm.RateLimitAlgorithm;
import io.github.pawankm21.rate_limiter_service.dto.ClientType;
import io.github.pawankm21.rate_limiter_service.entity.Client;
import io.github.pawankm21.rate_limiter_service.entity.Policy;
import io.github.pawankm21.rate_limiter_service.repository.ClientRepository;
import io.github.pawankm21.rate_limiter_service.repository.PolicyRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.ratelimit.AbstractRateLimiter;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.cloud.gateway.support.ConfigurationService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Adapts the custom {@link RateLimitAlgorithm} to Spring Cloud Gateway's
 * {@link RateLimiter} SPI so a route can use it through the {@code RequestRateLimiter}
 * filter.
 *
 * <p>Unlike the in-memory algorithm, the Redis-backed implementation shares bucket state
 * across gateway instances.
 */
@Component
@Primary
@ConditionalOnProperty(name = "rate-limiter.gateway.enabled", havingValue = "true")
public class CustomRateLimiter extends AbstractRateLimiter<CustomRateLimiter.Config> {

    public static final String CONFIGURATION_PROPERTY_NAME = "custom-rate-limiter";

    private final RateLimitAlgorithm algorithm;
    private final ClientRepository clientRepository;
    private final PolicyRepository policyRepository;

    public CustomRateLimiter(RateLimitAlgorithm algorithm, ClientRepository clientRepository,
            PolicyRepository policyRepository, ConfigurationService configurationService) {
        super(Config.class, CONFIGURATION_PROPERTY_NAME, configurationService);
        this.algorithm = algorithm;
        this.clientRepository = clientRepository;
        this.policyRepository = policyRepository;
    }

    @Override
    public Mono<Response> isAllowed(String routeId, String id) {
        return Mono.fromCallable(() -> {
            long clientId = Long.parseLong(id);
            Policy policy = resolvePolicy(clientId);
            boolean allowed = algorithm.isRequestAllowed(clientId, new Date(), policy);
            return new Response(allowed, Map.of());
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private Policy resolvePolicy(long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown client: " + clientId));

        return firstPolicy(client.getClientType())
                .orElseGet(() -> firstPolicy(ClientType.FREE)
                        .orElseThrow(() -> new IllegalStateException(
                                "No rate-limit policy configured for client type " + client.getClientType())));
    }

    private Optional<Policy> firstPolicy(ClientType clientType) {
        List<Policy> policies = policyRepository.findByClientType(clientType);
        return policies.stream().findFirst();
    }

    public static class Config {

        private int requestedTokens = 1;

        public int getRequestedTokens() {
            return requestedTokens;
        }

        public Config setRequestedTokens(int requestedTokens) {
            this.requestedTokens = requestedTokens;
            return this;
        }

    }
}
