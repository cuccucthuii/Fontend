package org.example.cinema_reservation_system.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.example.cinema_reservation_system.entity.UserAccount;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    private final Key key;

    public JwtService(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    //sửa đoạn này
    public String generateToken(UserAccount user) {
        // Lấy mã vai trò chuẩn
        String raw = user.getVaiTro() != null ? user.getVaiTro().getTenVaiTro() : "USER";
        String roleCode;
        switch ((raw == null ? "" : raw).trim().toUpperCase()) {
            case "ADMIN":
            case "QUẢN LÝ":
            case "QUAN LY":
            case "QUẢN TRỊ VIÊN":
                roleCode = "ADMIN"; break;
            case "STAFF":
            case "NHÂN VIÊN":
            case "NHAN VIEN":
                roleCode = "STAFF"; break;
            default:
                roleCode = "USER";
        }

        return Jwts.builder()
                .setSubject(user.getTenDangNhap())
                .claim("role", roleCode)        // dùng mã chuẩn
                .claim("type", "access")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(UserAccount user) {
        return Jwts.builder()
                .setSubject(user.getTenDangNhap())
                .claim("type", "refresh")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)) // 7 ngày
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateSecureRefreshToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[64];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String extractRole(String token) {
        return (String) Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role");
    }

    public String extractTokenType(String token) {
        return (String) Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("type");
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            return expiration.before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return true;
        }
    }

    public boolean isRefreshToken(String token) {
        try {
            String type = extractTokenType(token);
            return "refresh".equals(type);
        } catch (Exception e) {
            return false;
        }
    }
}
