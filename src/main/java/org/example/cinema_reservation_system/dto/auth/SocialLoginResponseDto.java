package org.example.cinema_reservation_system.dto.auth;

import lombok.Data;

@Data
public class SocialLoginResponseDto {
    
    private String token;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    
    private Integer userId;
    private String username;
    private String email;
    private String role;
    private String avatarUrl;
    private String provider;
    private Boolean isNewUser;
    private String message;
}
