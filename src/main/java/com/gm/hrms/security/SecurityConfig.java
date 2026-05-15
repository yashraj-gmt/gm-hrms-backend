package com.gm.hrms.security;

import com.gm.hrms.config.CustomUserDetailsService;
import com.gm.hrms.config.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@EnableMethodSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtFilter,
            AuthenticationProvider authenticationProvider,
            CustomAuthEntryPoint customAuthEntryPoint,
            CustomAccessDeniedHandler customAccessDeniedHandler,
            CorsConfigurationSource corsConfigurationSource          // ← from CorsConfig
    ) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .authenticationProvider(authenticationProvider)
                .authorizeHttpRequests(auth -> auth

                        // ── Public auth endpoints ─────────────────────────────
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/refresh",
                                "/api/auth/forgot-password",   // ← WAS MISSING
                                "/api/auth/verify-otp",        // ← WAS MISSING
                                "/api/auth/reset-password"     // ← WAS MISSING
                        ).permitAll()

                        // ── Protected auth endpoints ──────────────────────────
                        .requestMatchers("/api/auth/change-password").authenticated()
                        .requestMatchers("/api/auth/logout").authenticated()

                        // ── Role-gated endpoints ───────────────────────────────
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/hr/**").hasAnyRole("ADMIN", "HR")
                        .requestMatchers("/api/employee/**").hasAnyRole("ADMIN", "HR", "EMPLOYEE")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//        @Bean
//    public CommandLineRunner run(PasswordEncoder encoder) {
//        return args -> {
//            String password = "GMT@123";
//            String hash = encoder.encode(password);
//
//            System.out.println("Generated Hash: " + hash);
//        };
//    }


    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
//    @Bean
//    public CommandLineRunner run(PasswordEncoder encoder) {
//        return args -> {
//            String password = "GMhrms@123";
//            String hash = encoder.encode(password);
//
//            System.out.println("Generated Hash: " + hash);
//        };
//    }
