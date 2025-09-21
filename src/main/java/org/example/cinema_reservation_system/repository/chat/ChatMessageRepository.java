package org.example.cinema_reservation_system.repository.chat;

import org.example.cinema_reservation_system.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
    
    // Lấy tin nhắn theo room ID, sắp xếp theo thời gian tạo
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.roomId = :roomId AND cm.isDeleted = false ORDER BY cm.createdAt ASC")
    List<ChatMessage> findByRoomIdOrderByCreatedAtAsc(@Param("roomId") String roomId);
    
    // Lấy tin nhắn theo room ID với phân trang
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.roomId = :roomId AND cm.isDeleted = false ORDER BY cm.createdAt DESC")
    Page<ChatMessage> findByRoomIdOrderByCreatedAtDesc(@Param("roomId") String roomId, Pageable pageable);
    
    // Đếm tin nhắn chưa đọc theo room ID
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.roomId = :roomId AND cm.isRead = false AND cm.isDeleted = false")
    Long countUnreadMessagesByRoomId(@Param("roomId") String roomId);
    
    // Đếm tin nhắn chưa đọc theo room ID và sender type
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.roomId = :roomId AND cm.senderType != :senderType AND cm.isRead = false AND cm.isDeleted = false")
    Long countUnreadMessagesByRoomIdAndNotSenderType(@Param("roomId") String roomId, @Param("senderType") String senderType);
    
    // Lấy tin nhắn cuối cùng của room
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.roomId = :roomId AND cm.isDeleted = false ORDER BY cm.createdAt DESC")
    List<ChatMessage> findLastMessageByRoomId(@Param("roomId") String roomId, Pageable pageable);

    // Lấy N tin nhắn mới nhất (dùng làm memory cho AI)
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.roomId = :roomId AND cm.isDeleted = false ORDER BY cm.createdAt DESC")
    List<ChatMessage> findLastNByRoomId(@Param("roomId") String roomId, Pageable pageable);

    default List<ChatMessage> findLastNByRoomId(String roomId, int n) {
        return findLastNByRoomId(roomId, org.springframework.data.domain.PageRequest.of(0, Math.max(1, n)));
    }
    
    // Đánh dấu tin nhắn đã đọc
    @Modifying
    @Query("UPDATE ChatMessage cm SET cm.isRead = true, cm.readAt = :readAt WHERE cm.roomId = :roomId AND cm.senderType != :senderType AND cm.isRead = false")
    void markMessagesAsRead(@Param("roomId") String roomId, @Param("senderType") String senderType, @Param("readAt") LocalDateTime readAt);
    
    // Xóa tin nhắn (soft delete)
    @Modifying
    @Query("UPDATE ChatMessage cm SET cm.isDeleted = true WHERE cm.idMessage = :messageId")
    void softDeleteMessage(@Param("messageId") Integer messageId);
    
    // Lấy tin nhắn theo thời gian
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.roomId = :roomId AND cm.createdAt > :since AND cm.isDeleted = false ORDER BY cm.createdAt ASC")
    List<ChatMessage> findNewMessagesSince(@Param("roomId") String roomId, @Param("since") LocalDateTime since);
    
    // Tìm tin nhắn theo nội dung (search)
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.roomId = :roomId AND cm.messageContent LIKE %:keyword% AND cm.isDeleted = false ORDER BY cm.createdAt DESC")
    List<ChatMessage> searchMessagesByContent(@Param("roomId") String roomId, @Param("keyword") String keyword);
}

