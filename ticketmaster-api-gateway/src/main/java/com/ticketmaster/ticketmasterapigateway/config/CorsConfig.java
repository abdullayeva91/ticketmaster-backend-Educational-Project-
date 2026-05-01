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

        // ============================================================
        // ALLOWED ORIGINS (Bütün Vercel və Lokal variantlara icazə veririk)
        // ============================================================
        corsConfig.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:3000",
                "https://ticketmaster-frontend-*.vercel.app", // Bütün Vercel alt-domenləri üçün
                "https://ticketmaster-frontend-ten.vercel.app"
        ));

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
        // CREDENTIALS
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