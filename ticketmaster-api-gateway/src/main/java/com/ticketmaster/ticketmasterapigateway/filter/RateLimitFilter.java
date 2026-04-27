package com.ticketmaster.ticketmasterapigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
public class RateLimitFilter extends AbstractGatewayFilterFactory<RateLimitFilter.Config> {

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    @Value("${rate-limit.max-requests:100}")
    private int maxRequests;

    @Value("${rate-limit.window-seconds:60}")
    private int windowSeconds;

    public RateLimitFilter(ReactiveRedisTemplate<String, String> redisTemplate) {
        super(Config.class);
        this.redisTemplate = redisTemplate;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String clientId = getClientId(exchange);
            String key = "rate_limit:" + clientId;

            return redisTemplate.opsForValue()
                    .increment(key)
                    .flatMap(count -> {
                        if (count == 1) {
                            return redisTemplate.expire(key, Duration.ofSeconds(windowSeconds))
                                    .then(Mono.just(count));
                        }
                        return Mono.just(count);
                    })
                    .flatMap(count -> {
                        exchange.getResponse().getHeaders().add("X-Rate-Limit-Limit", String.valueOf(maxRequests));
                        exchange.getResponse().getHeaders().add("X-Rate-Limit-Remaining",
                                String.valueOf(Math.max(0, maxRequests - count)));
                        exchange.getResponse().getHeaders().add("X-Rate-Limit-Reset", String.valueOf(windowSeconds));

                        if (count > maxRequests) {
                            log.warn("Rate limit exceeded for client: {} - Request count: {}", clientId, count);
                            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                            exchange.getResponse().getHeaders().add("Content-Type", "application/json");

                            String errorResponse = String.format(
                                    "{\"error\": \"Rate limit exceeded\", \"limit\": %d, \"window\": \"%d seconds\"}",
                                    maxRequests, windowSeconds);

                            return exchange.getResponse().writeWith(
                                    Mono.just(exchange.getResponse().bufferFactory().wrap(errorResponse.getBytes())));
                        }

                        log.debug("Rate limit check passed for client: {} - Request count: {}/{}",
                                clientId, count, maxRequests);

                        return chain.filter(exchange);
                    })
                    .onErrorResume(e -> {
                        log.error("Rate limiting error for client {}: {}", clientId, e.getMessage());
                        return chain.filter(exchange);
                    });
        };
    }

    private String getClientId(ServerWebExchange exchange) {
        List<String> userIdHeaders = exchange.getRequest().getHeaders().get("X-User-Id");
        if (userIdHeaders != null && !userIdHeaders.isEmpty()) {
            String userId = userIdHeaders.get(0);
            if (userId != null && !userId.isEmpty()) {
                return "user:" + userId;
            }
        }

        List<String> forwardedHeaders = exchange.getRequest().getHeaders().get("X-Forwarded-For");
        if (forwardedHeaders != null && !forwardedHeaders.isEmpty()) {
            String forwardedFor = forwardedHeaders.get(0);
            if (forwardedFor != null && !forwardedFor.isEmpty()) {
                return "ip:" + forwardedFor.split(",")[0].trim();
            }
        }

        return exchange.getRequest().getRemoteAddress() != null
                ? "ip:" + exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                : "ip:unknown";
    }

    public static class Config {
    }
}