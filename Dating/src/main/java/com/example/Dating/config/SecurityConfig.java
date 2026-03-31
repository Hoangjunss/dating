package com.example.Dating.config;

import com.example.Dating.security.JwtAuthenticationFilter;
import com.example.Dating.filter.RateLimitFilter;
import com.example.Dating.security.JwtAccessDeniedHandler;
import com.example.Dating.security.JwtAuthEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security configuration.
 *
 * Chiến lược:
 *  - Stateless (JWT, không dùng session/cookie)
 *  - Public: /api/auth/**, /ws/** (WebSocket handshake)
 *  - Protected: tất cả endpoint còn lại cần Bearer token hợp lệ
 *  - Method-level security (@PreAuthorize) cho kiểm tra ownership
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity           // bật @PreAuthorize, @PostAuthorize
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RateLimitFilter rateLimitFilter;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ── Disable CSRF (stateless JWT) ──────────────────────────────────
                .csrf(AbstractHttpConfigurer::disable)

                // ── CORS ─────────────────────────────────────────────────────────
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // ── Session: STATELESS ────────────────────────────────────────────
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ── Exception Handling ────────────────────────────────────────────
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler))

                // ── Security Headers ──────────────────────────────────────────────
                .headers(headers -> headers
                        .frameOptions(fo -> fo.deny())
                        .xssProtection(xss -> xss.disable())            // dùng CSP thay vì X-XSS
                        .contentSecurityPolicy(csp ->
                                csp.policyDirectives("default-src 'self'; frame-ancestors 'none'"))
                        .referrerPolicy(rp ->
                                rp.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                )

                // ── Authorization Rules ───────────────────────────────────────────
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/auth/login", "/api/auth/register",
                                "/api/auth/refresh").permitAll()

                        // WebSocket handshake (SockJS)
                        .requestMatchers("/ws/**").permitAll()

                        // Admin-only: Interest CRUD (master data)
                        // Nếu chưa có ADMIN role, tạm set authenticated()
                        .requestMatchers(HttpMethod.POST,   "/api/interests").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/interests/**").authenticated()

                        // Tất cả endpoint còn lại phải authenticated
                        .anyRequest().authenticated()
                )

                // ── Filters ───────────────────────────────────────────────────────
                .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",   // Vite dev
                "http://localhost:3000"    // CRA dev (nếu cần)
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
