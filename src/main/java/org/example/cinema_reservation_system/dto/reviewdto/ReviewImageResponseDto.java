package org.example.cinema_reservation_system.dto.reviewdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewImageResponseDto {
    
    private Long id;
    private Long reviewId;
    private String imageUrl;
    private String description;
    private LocalDateTime createdAt;
}

