package com.example.Dating.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/**
 * WebConfig — CORS đã được cấu hình tập trung trong SecurityConfig.corsConfigurationSource().
 *
 * Loại bỏ WebMvcConfigurer CORS riêng để tránh xung đột với
 * Spring Security filter chain. Nếu cả hai cùng tồn tại, CORS của WebMvcConfigurer
 * có thể bypass Security filters trên một số request.
 *
 * Tất cả CORS config nằm trong:
 *   SecurityConfig.corsConfigurationSource() → UrlBasedCorsConfigurationSource
 */
@Configuration
public class WebConfig {
}