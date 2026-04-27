package com.ticketmaster.ticketmasterapigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {

    public LoggingFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String requestPath = request.getPath().toString();
            String requestMethod = request.getMethod() != null ? request.getMethod().toString() : "UNKNOWN";
            String clientIp = getClientIp(exchange);
            long startTime = System.currentTimeMillis();

            log.info("==> Incoming Request: {} {} from {} at {}",
                    requestMethod,
                    requestPath,
                    clientIp,
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                int statusCode = exchange.getResponse().getStatusCode() != null
                        ? exchange.getResponse().getStatusCode().value()
                        : 0;

                log.info("<== Response: {} {} - Status: {} - Duration: {}ms",
                        requestMethod,
                        requestPath,
                        statusCode,
                        duration);

                if (duration > 3000) {
                    log.warn("SLOW REQUEST DETECTED: {} {} took {}ms", requestMethod, requestPath, duration);
                }
            }));
        };
    }

    private String getClientIp(ServerWebExchange exchange) {
        List<String> forwardedHeaders = exchange.getRequest().getHeaders().get("X-Forwarded-For");
        if (forwardedHeaders != null && !forwardedHeaders.isEmpty()) {
            String forwardedFor = forwardedHeaders.get(0);
            if (forwardedFor != null && !forwardedFor.isEmpty()) {
                return forwardedFor.split(",")[0].trim();
            }
        }
        return exchange.getRequest().getRemoteAddress() != null
                ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                : "unknown";
    }

    public static class Config {
    }
}