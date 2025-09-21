package org.example.cinema_reservation_system.dto.reviewdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.cinema_reservation_system.utils.enums.TrangThai;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewReplyResponseDto {
    
    private Long id;
    private Long reviewId;
    private Long employeeId;
    private String employeeName;
    private String replyContent;
    private LocalDateTime repliedAt;
    private TrangThai status;
}

