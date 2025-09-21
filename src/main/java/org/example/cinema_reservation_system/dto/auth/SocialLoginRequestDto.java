package org.example.cinema_reservation_system.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SocialLoginRequestDto {
    
    @NotBlank(message = "Access token không được để trống")
    private String accessToken;
    
    @NotNull(message = "Provider không được để trống")
    private String provider; // GOOGLE, FACEBOOK
    
    private String email;
    private String name;
    private String picture;
    private String socialId;
}
