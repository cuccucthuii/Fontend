package org.example.cinema_reservation_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Rate Limiting Configuration
 * 
 * Implements simple in-memory rate limiting to prevent API abuse
 * In production, consider using Redis-based rate limiting
 */
@Configuration
public class RateLimitingConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitingInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/login", "/api/auth/register"); // Exclude auth endpoints
    }

    @Bean
    public RateLimitingInterceptor rateLimitingInterceptor() {
        return new RateLimitingInterceptor();
    }

    /**
     * Simple Rate Limiting Interceptor
     * Allows 100 requests per minute per IP
     */
    public static class RateLimitingInterceptor implements org.springframework.web.servlet.HandlerInterceptor {
        
        private final ConcurrentHashMap<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
        private final ConcurrentHashMap<String, Long> resetTimes = new ConcurrentHashMap<>();
        
        // Rate limit: 100 requests per minute
        private static final int MAX_REQUESTS = 100;
        private static final long WINDOW_SIZE = 60 * 1000; // 1 minute in milliseconds
        
        @Override
        public boolean preHandle(jakarta.servlet.http.HttpServletRequest request, 
                               jakarta.servlet.http.HttpServletResponse response, 
                               Object handler) throws Exception {
            
            String clientIp = getClientIpAddress(request);
            long currentTime = System.currentTimeMillis();
            
            // Clean up old entries
            cleanupOldEntries(currentTime);
            
            // Get or create counter for this IP
            AtomicInteger count = requestCounts.computeIfAbsent(clientIp, k -> new AtomicInteger(0));
            Long resetTime = resetTimes.computeIfAbsent(clientIp, k -> currentTime + WINDOW_SIZE);
            
            // Check if window has expired
            if (currentTime > resetTime) {
                count.set(0);
                resetTimes.put(clientIp, currentTime + WINDOW_SIZE);
            }
            
            // Check rate limit
            if (count.incrementAndGet() > MAX_REQUESTS) {
                response.setStatus(429); // Too Many Requests
                response.getWriter().write("{\"error\":\"Rate limit exceeded. Please try again later.\"}");
                response.setContentType("application/json");
                return false;
            }
            
            return true;
        }
        
        private String getClientIpAddress(jakarta.servlet.http.HttpServletRequest request) {
            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                return xForwardedFor.split(",")[0].trim();
            }
            
            String xRealIp = request.getHeader("X-Real-IP");
            if (xRealIp != null && !xRealIp.isEmpty()) {
                return xRealIp;
            }
            
            return request.getRemoteAddr();
        }
        
        private void cleanupOldEntries(long currentTime) {
            // Remove entries older than 2 minutes to prevent memory leaks
            resetTimes.entrySet().removeIf(entry -> 
                entry.getValue() < currentTime - (2 * WINDOW_SIZE));
            requestCounts.entrySet().removeIf(entry -> 
                !resetTimes.containsKey(entry.getKey()));
        }
    }
}




























