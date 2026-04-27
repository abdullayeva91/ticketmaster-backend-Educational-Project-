package com.ticketmaster.ticketmasterapigateway.filter;

import com.ticketmaster.ticketmasterapigateway.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final JwtUtil jwtUtil;

    public AuthenticationFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    private boolean isSecured(ServerHttpRequest request) {
        String path = request.getPath().toString();
        String method = request.getMethod() != null ? request.getMethod().name() : "UNKNOWN";

        if (path.startsWith("/api/v1/auth/")) {
            return false;
        }

        if (path.contains("/api/v1/users/") && path.contains("/exists")) {
            return false;
        }

        if (method.equals("GET") && (
                path.startsWith("/api/v1/events") ||
                        path.startsWith("/api/v1/categories") ||
                        path.startsWith("/api/v1/venues"))) {
            return false;
        }

        return true;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            log.debug("Authentication filter - Path: {}", request.getPath());

            if (!isSecured(request)) {
                log.info("Public endpoint accessed: {} {}", request.getMethod(), request.getPath());
                return chain.filter(exchange);
            }

            List<String> authHeaders = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
            if (authHeaders == null || authHeaders.isEmpty()) {
                log.warn("Missing Authorization header for path: {}", request.getPath());
                return onError(exchange, "Missing authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = authHeaders.get(0);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Invalid Authorization header format");
                return onError(exchange, "Invalid authorization header format", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);

            try {
                if (!jwtUtil.validateToken(token)) {
                    log.warn("Invalid or expired token");
                    return onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
                }

                String username = jwtUtil.extractUsername(token);
                String userId = jwtUtil.extractUserId(token);
                String role = jwtUtil.extractRole(token);

                log.info("User authenticated: username={}, userId={}, role={}", username, userId, role);

                ServerHttpRequest modifiedRequest = exchange.getRequest()
                        .mutate()
                        .header("X-User-Id", userId != null ? userId : username)
                        .header("X-Username", username)
                        .header("X-User-Role", role != null ? role : "USER")
                        .build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build());

            } catch (Exception e) {
                log.error("Authentication error: {}", e.getMessage());
                return onError(exchange, "Authentication failed: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().add("Content-Type", "application/json");

        String errorResponse = String.format("{\"error\": \"%s\", \"status\": %d, \"path\": \"%s\"}",
                message, httpStatus.value(), exchange.getRequest().getPath());

        log.error("Authentication error: {} - Path: {}", message, exchange.getRequest().getPath());

        return response.writeWith(Mono.just(response.bufferFactory().wrap(errorResponse.getBytes())));
    }

    public static class Config {
    }
}