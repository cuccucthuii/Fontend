package org.example.cinema_reservation_system.dto.chatdto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChatRoomRequestDto {
    
    @NotNull(message = "ID khách hàng không được để trống")
    private Integer customerId;
    
    private String roomName; // Tên phòng chat (tự động tạo nếu không có)
    
    private String tags; // Tags để phân loại vấn đề
}

