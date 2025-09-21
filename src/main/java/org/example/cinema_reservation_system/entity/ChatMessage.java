package org.example.cinema_reservation_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.cinema_reservation_system.utils.enums.MessageType;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chat_messages")
public class ChatMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_message")
    private Integer idMessage;
    
    @Column(name = "room_id", nullable = false)
    private String roomId; // ID phòng chat (có thể là user_id hoặc session_id)
    
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private UserAccount sender; // Người gửi tin nhắn
    
    @Column(name = "sender_name", nullable = false)
    private String senderName; // Tên người gửi
    
    @Column(name = "sender_type", nullable = false)
    private String senderType; // "CUSTOMER", "STAFF", "ADMIN"
    
    @Column(name = "message_content", columnDefinition = "TEXT", nullable = false)
    private String messageContent; // Nội dung tin nhắn
    
    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType = MessageType.TEXT; // TEXT, IMAGE, FILE, SYSTEM
    
    @Column(name = "file_url")
    private String fileUrl; // URL file nếu là tin nhắn file/image
    
    @Column(name = "file_name")
    private String fileName; // Tên file
    
    @Column(name = "file_size")
    private Long fileSize; // Kích thước file (bytes)
    
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false; // Đã đọc chưa
    
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false; // Đã xóa chưa
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "read_at")
    private LocalDateTime readAt; // Thời gian đọc
    
    @Column(name = "reply_to_message_id")
    private Integer replyToMessageId; // ID tin nhắn được reply
    
    @Column(name = "message_status", length = 20)
    private String messageStatus = "SENT"; // SENT, DELIVERED, READ, FAILED
    
    // Pre-persist để tự động set thời gian tạo
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

