package org.example.cinema_reservation_system.dto.reviewdto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewImageRequestDto {
    
    @NotNull(message = "ID đánh giá phim không được để trống")
    private Long reviewId;
    
    @NotBlank(message = "URL hình ảnh không được để trống")
    private String imageUrl;
    
    private String description;
}

