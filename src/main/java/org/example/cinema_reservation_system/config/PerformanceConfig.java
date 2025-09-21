package org.example.cinema_reservation_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.extern.slf4j.Slf4j;

/**
 * Performance Monitoring Configuration
 * 
 * Monitors API response times and logs slow queries
 */
@Slf4j
@Configuration
public class PerformanceConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(performanceInterceptor());
    }

    @Bean
    public PerformanceInterceptor performanceInterceptor() {
        return new PerformanceInterceptor();
    }

    /**
     * Performance Monitoring Interceptor
     * Logs slow API calls (>1000ms)
     */
    public static class PerformanceInterceptor implements org.springframework.web.servlet.HandlerInterceptor {
        
        private static final long SLOW_QUERY_THRESHOLD = 1000; // 1 second
        
        @Override
        public boolean preHandle(jakarta.servlet.http.HttpServletRequest request, 
                               jakarta.servlet.http.HttpServletResponse response, 
                               Object handler) throws Exception {
            long startTime = System.currentTimeMillis();
            request.setAttribute("startTime", startTime);
            return true;
        }
        
        @Override
        public void afterCompletion(jakarta.servlet.http.HttpServletRequest request, 
                                  jakarta.servlet.http.HttpServletResponse response, 
                                  Object handler, Exception ex) throws Exception {
            Long startTime = (Long) request.getAttribute("startTime");
            if (startTime != null) {
                long duration = System.currentTimeMillis() - startTime;
                
                if (duration > SLOW_QUERY_THRESHOLD) {
                    log.warn("SLOW API CALL: {} {} took {}ms", 
                        request.getMethod(), 
                        request.getRequestURI(), 
                        duration);
                } else {
                    log.debug("API CALL: {} {} took {}ms", 
                        request.getMethod(), 
                        request.getRequestURI(), 
                        duration);
                }
            }
        }
    }
}




























