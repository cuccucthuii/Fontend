package org.example.cinema_reservation_system.dto.reviewdto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewReplyRequestDto {
    
    @NotNull(message = "ID đánh giá phim không được để trống")
    private Long reviewId;
    
    @NotBlank(message = "Nội dung phản hồi không được để trống")
    private String replyContent;
    
    private Long employeeId; // Có thể null nếu là phản hồi tự động
}

