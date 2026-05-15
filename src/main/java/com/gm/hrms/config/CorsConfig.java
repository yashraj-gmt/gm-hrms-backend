package com.gm.hrms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {


    @Value("${cors.allowed-origins:http://localhost:5173,http://localhost:3000}")
    private String allowedOriginsRaw;

    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter(corsConfigurationSource());
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        // ── Origins ──────────────────────────────────────────────────────────
        List<String> origins = List.of(allowedOriginsRaw.split(","));
        config.setAllowedOrigins(origins.stream()
                .map(String::trim)
                .toList());

        // ── Methods ──────────────────────────────────────────────────────────
        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        // ── Headers ──────────────────────────────────────────────────────────
        config.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept",
                "X-Requested-With",
                "Origin"
        ));

        // ── Expose headers the frontend may need to read ──────────────────────
        config.setExposedHeaders(List.of("Authorization"));

        // ── Credentials (needed for Authorization header) ─────────────────────
        config.setAllowCredentials(true);

        // ── Preflight cache: 1 hour ───────────────────────────────────────────
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);   // covers all /api/* routes
        return source;
    }
}