package org.example.cinema_reservation_system.security;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Dùng config bên dưới
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        // ========== PUBLIC ENDPOINTS (Không cần đăng nhập) ==========
                        // Cho phép test nhanh: mở toàn bộ GET dưới /api/**
                        .requestMatchers(HttpMethod.GET, "/api/**").permitAll()
                        // Xem danh sách phim
                        .requestMatchers(HttpMethod.GET, "/api/phim/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/the-loai/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/trailer/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/rap_chieu/**").permitAll()
                        // Phòng chiếu (controller dùng path với dấu gạch dưới)
                        .requestMatchers(HttpMethod.GET, "/api/phong_chieu").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/phong_chieu/**").permitAll()
                        // Suất chiếu
                        .requestMatchers(HttpMethod.GET, "/api/suat-chieu").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/suat-chieu/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/ghengoi/**").permitAll()
                        // Food & Combos (public GET for testing/catalog)
                        .requestMatchers(HttpMethod.GET, "/api/food-items").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/food-items/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/combos").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/combos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/food").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/food/**").permitAll()
                        
                        // Đăng ký và đăng nhập
                        .requestMatchers(HttpMethod.POST, "/api/auth/register/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/send-otp").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/verify-otp").permitAll()
                        
                        // Social Login (Google, Facebook)
                        .requestMatchers(HttpMethod.POST, "/api/auth/social/**").permitAll()
                        
                        // Upload files (public để xem ảnh)
                        .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()
                        
                        // Barcode generation (public để test)
                        .requestMatchers(HttpMethod.GET, "/api/barcode/**").permitAll()
                        
                        // Swagger UI
                        .requestMatchers("/swagger-ui/**", "/api-docs/**").permitAll()
                        
                        // ========== AUTHENTICATED ENDPOINTS (Cần đăng nhập) ==========
                        // Đặt vé và quản lý vé
                        .requestMatchers(HttpMethod.POST, "/api/bookings/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/bookings/history").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/bookings/{id}").authenticated()
                        
                        // Quản lý tài khoản cá nhân
                        .requestMatchers(HttpMethod.PUT, "/api/user/update-current-user").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/user/change-password").authenticated()
                        
                        // Thanh toán
                        .requestMatchers(HttpMethod.POST, "/api/payment/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/vnpay/create").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/vnpay/return").permitAll() // Callback VNPay không cần auth
                        
                        // Chat hỗ trợ khách hàng (cần đăng nhập)
                        .requestMatchers(HttpMethod.POST, "/api/chat/rooms").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/chat/rooms/{roomId}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/chat/customer/rooms").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/chat/messages").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/chat/rooms/{roomId}/messages/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/chat/rooms/{roomId}/messages/read").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/chat/messages/{messageId}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/chat/rooms/{roomId}/unread-count").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/chat/rooms/{roomId}/close").authenticated()
                        
                        // ========== STAFF ENDPOINTS (Nhân viên) ==========
                        // Check-in vé (chỉ nhân viên)
                        .requestMatchers(HttpMethod.POST, "/api/checkin/**").hasAnyRole("STAFF", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/checkin/**").hasAnyRole("STAFF", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/checkin/**").hasAnyRole("STAFF", "ADMIN")
                        
                        // Chat hỗ trợ (staff)
                        .requestMatchers(HttpMethod.GET, "/api/chat/staff/rooms").hasAnyRole("STAFF", "ADMIN")
                        
                        // ========== ADMIN ENDPOINTS ==========
                        // Quản lý phim (CRUD)
                        .requestMatchers(HttpMethod.POST, "/api/phim/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/phim/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/phim/**").hasRole("ADMIN")
                        
                        // Quản lý thể loại
                        .requestMatchers(HttpMethod.POST, "/api/the-loai/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/the-loai/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/the-loai/**").hasRole("ADMIN")
                        
                        // Quản lý rạp chiếu
                        .requestMatchers(HttpMethod.POST, "/api/rap_chieu/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/rap_chieu/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/rap_chieu/**").hasRole("ADMIN")
                        
                        // Quản lý phòng chiếu
                        .requestMatchers(HttpMethod.POST, "/api/phong-chieu/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/phong-chieu/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/phong-chieu/**").hasRole("ADMIN")
                        
                        // Quản lý suất chiếu
                        .requestMatchers(HttpMethod.POST, "/api/suat-chieu/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/suat-chieu/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/suat-chieu/**").hasRole("ADMIN")
                        
                        // Quản lý ghế ngồi
                        .requestMatchers(HttpMethod.POST, "/api/ghengoi/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/ghengoi/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/ghengoi/**").hasRole("ADMIN")
                        
                        // Quản lý người dùng
                        .requestMatchers(HttpMethod.GET, "/api/user/all").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/user/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/auth/register-employee").hasRole("ADMIN")
                        
                        // Chat hỗ trợ (admin)
                        .requestMatchers(HttpMethod.GET, "/api/chat/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/chat/admin/**").hasRole("ADMIN")
                        
                        // Thống kê
                        .requestMatchers("/api/statistics/**").hasRole("ADMIN")
                        
                        // Tất cả request khác cần đăng nhập
                        .anyRequest().authenticated()
                );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("http://localhost:5173")); // KHÔNG dùng "*"
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
