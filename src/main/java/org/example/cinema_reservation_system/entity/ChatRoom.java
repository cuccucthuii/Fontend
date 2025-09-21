package org.example.cinema_reservation_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chat_rooms")
public class ChatRoom {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_room")
    private Integer idRoom;
    
    @Column(name = "room_id", unique = true, nullable = false)
    private String roomId; // Unique room ID
    
    @Column(name = "room_name")
    private String roomName; // Tên phòng chat
    
    @Column(name = "room_type", nullable = false)
    private String roomType; // "CUSTOMER_SUPPORT", "STAFF_CHAT", "ADMIN_CHAT"
    
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer; // Khách hàng (nếu là chat support)
    
    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Staff staff; // Nhân viên hỗ trợ
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true; // Phòng chat có đang hoạt động không
    
    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt; // Thời gian tin nhắn cuối cùng
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "closed_at")
    private LocalDateTime closedAt; // Thời gian đóng phòng chat
    
    @Column(name = "status", length = 20)
    private String status = "OPEN"; // OPEN, CLOSED, PENDING
    
    @Column(name = "priority", length = 20)
    private String priority = "NORMAL"; // LOW, NORMAL, HIGH, URGENT
    
    @Column(name = "tags")
    private String tags; // Tags để phân loại (ví dụ: "technical", "billing", "general")
    
    // One-to-Many với ChatMessage
    @OneToMany(mappedBy = "roomId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChatMessage> messages;
    
    // Pre-persist để tự động set thời gian tạo
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastMessageAt = LocalDateTime.now();
    }
}

