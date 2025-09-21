package org.example.cinema_reservation_system.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvatarDto {
    
    private Long id;
    private String avatarName;
    private String avatarUrl;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
}

