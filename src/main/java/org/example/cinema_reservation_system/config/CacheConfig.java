package org.example.cinema_reservation_system.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Cache Configuration for Performance Optimization
 * 
 * Caches frequently accessed data to reduce database load:
 * - Movies by status
 * - Theaters and rooms
 * - User information
 * - Promotion data
 */
@Configuration
@EnableCaching
@EnableScheduling
public class CacheConfig {

    /**
     * Cache Manager Configuration
     * Using ConcurrentMapCacheManager for simplicity
     * In production, consider using Redis or Hazelcast
     */
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        
        // Define cache names
        cacheManager.setCacheNames(java.util.Arrays.asList(
            "movies",           // Movie data cache
            "moviesByStatus",   // Movies filtered by status
            "theaters",         // Theater information
            "rooms",            // Room information
            "showtimes",        // Showtime data
            "genres",           // Genre information
            "users",            // User data cache
            "promotions",       // Promotion data
            "foodItems",        // Food items cache
            "statistics"        // Statistics cache
        ));
        
        // Allow dynamic cache creation
        cacheManager.setAllowNullValues(false);
        
        return cacheManager;
    }
}




























