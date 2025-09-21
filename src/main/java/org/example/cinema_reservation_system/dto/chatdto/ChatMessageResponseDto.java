package org.example.cinema_reservation_system.dto.chatdto;

import lombok.Data;
import org.example.cinema_reservation_system.utils.enums.MessageType;

import java.time.LocalDateTime;

@Data
public class ChatMessageResponseDto {
    
    private Integer idMessage;
    private String roomId;
    private Integer senderId;
    private String senderName;
    private String senderType; // CUSTOMER, STAFF, ADMIN
    private String messageContent;
    private MessageType messageType;
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    private Integer replyToMessageId;
    private String messageStatus;
    private String senderAvatar; // Avatar của người gửi
    private Boolean isOwnMessage; // Có phải tin nhắn của mình không
}

