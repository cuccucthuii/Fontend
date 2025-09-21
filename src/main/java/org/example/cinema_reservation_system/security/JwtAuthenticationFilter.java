package org.example.cinema_reservation_system.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.entity.UserAccount;
import org.example.cinema_reservation_system.repository.useraccount.UserAccountRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserAccountRepository userAccountRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        try {
            String token = extractTokenFromRequest(request);
            
            if (StringUtils.hasText(token) && jwtService.validateToken(token)) {
                String username = jwtService.extractUsername(token);
                String role = jwtService.extractRole(token);
                String normalizedRole = normalizeRole(role);
                
                UserAccount user = userAccountRepository.findByTenDangNhap(username)
                        .orElse(null);
                
                if (user != null) {
                    UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    null,
                                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + normalizedRole))
                            );
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: " + e.getMessage(), e);
        }
        
        filterChain.doFilter(request, response);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private String normalizeRole(String rawRole) {
        if (rawRole == null) return "CUSTOMER";
        String r = rawRole.trim().toLowerCase();
        switch (r) {
            case "admin":
            case "quản lý":
            case "quan ly":
                return "ADMIN";
            case "staff":
            case "nhân viên":
            case "nhan vien":
                return "STAFF";
            case "customer":
            case "khách hàng":
            case "khach hang":
                return "CUSTOMER";
            default:
                // Nếu hệ thống có role khác, mặc định coi là CUSTOMER để an toàn
                return rawRole.toUpperCase();
        }
    }
}

