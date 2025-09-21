package org.example.cinema_reservation_system.dto.chatdto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.cinema_reservation_system.utils.enums.MessageType;

@Data
public class ChatMessageRequestDto {
    
    @NotBlank(message = "Room ID không được để trống")
    private String roomId;
    
    @NotBlank(message = "Nội dung tin nhắn không được để trống")
    private String messageContent;
    
    @NotNull(message = "Loại tin nhắn không được để trống")
    private MessageType messageType = MessageType.TEXT;
    
    private String fileUrl; // URL file nếu có
    
    private String fileName; // Tên file nếu có
    
    private Long fileSize; // Kích thước file nếu có
    
    private Integer replyToMessageId; // ID tin nhắn được reply
}

