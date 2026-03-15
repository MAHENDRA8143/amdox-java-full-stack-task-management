package com.amdox.taskmanager.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    public WebConfig(JwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // ✅ Enable CORS for React frontend
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:3000", "http://localhost:5173")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // ✅ Register JWT interceptor for protected endpoints
        registry.addInterceptor(jwtInterceptor)
            .addPathPatterns("/**")           // Apply to all endpoints
            .excludePathPatterns(
                "/auth/**",                       // Exclude auth endpoints
                "/integrations/**",               // Exclude webhook endpoints
                "/h2-console/**",                 // Exclude H2 console (if using)
                "/error",                         // Exclude error pages
                "/actuator/**"                    // Exclude actuator endpoints (if using)
            );
    }
}