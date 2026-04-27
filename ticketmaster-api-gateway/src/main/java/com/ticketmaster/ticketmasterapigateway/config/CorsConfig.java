package com.ticketmaster.ticketmasterapigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        // ============================================
        // ALLOWED ORIGINS (React-in portuna icazə veririk)
        // ============================================
        corsConfig.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));

        // ============================================
        // ALLOWED METHODS
        // ============================================
        corsConfig.setAllowedMethods(Arrays.asList(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS",
                "PATCH"
        ));

        // ============================================
        // ALLOWED HEADERS
        // ============================================
        corsConfig.setAllowedHeaders(Collections.singletonList("*"));

        // ============================================
        // EXPOSED HEADERS
        // ============================================
        corsConfig.setExposedHeaders(Arrays.asList(
                "X-Rate-Limit-Limit",
                "X-Rate-Limit-Remaining",
                "X-Rate-Limit-Reset",
                "Authorization"
        ));

        // ============================================
        // CREDENTIALS (Tokenlərin keçməsi üçün TRUE edirik)
        // ============================================
        corsConfig.setAllowCredentials(true);

        // ============================================
        // MAX AGE
        // ============================================
        corsConfig.setMaxAge(3600L);

        // ============================================
        // APPLY CONFIGURATION
        // ============================================
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}