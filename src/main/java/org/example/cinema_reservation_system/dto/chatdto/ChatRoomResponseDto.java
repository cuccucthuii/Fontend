package org.example.cinema_reservation_system.dto.chatdto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatRoomResponseDto {
    
    private Integer idRoom;
    private String roomId;
    private String roomName;
    private String roomType;
    private Integer customerId;
    private String customerName;
    private String customerEmail;
    private Integer staffId;
    private String staffName;
    private Boolean isActive;
    private LocalDateTime lastMessageAt;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;
    private String status;
    private String priority;
    private String tags;
    private Integer unreadCount; // Số tin nhắn chưa đọc
    private String lastMessage; // Tin nhắn cuối cùng
    private String customerAvatar;
    private String staffAvatar;
}

