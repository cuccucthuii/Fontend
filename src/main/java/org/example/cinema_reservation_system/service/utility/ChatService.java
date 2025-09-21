package org.example.cinema_reservation_system.service.utility;

import org.example.cinema_reservation_system.dto.chatdto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import org.example.cinema_reservation_system.entity.Invoice;

public interface ChatService {
    
    // ========== CHAT ROOM MANAGEMENT ==========
    
    // Tạo phòng chat mới
    ChatRoomResponseDto createChatRoom(ChatRoomRequestDto request);
    
    // Lấy thông tin phòng chat
    ChatRoomResponseDto getChatRoom(String roomId);
    
    // Lấy danh sách phòng chat của customer
    List<ChatRoomResponseDto> getCustomerRooms(Integer customerId);
    
    // Lấy danh sách phòng chat của staff
    List<ChatRoomResponseDto> getStaffRooms(Integer staffId);
    
    // Lấy danh sách phòng chat đang mở (cho admin/staff)
    Page<ChatRoomResponseDto> getOpenRooms(Pageable pageable);
    
    // Lấy danh sách phòng chat chưa có staff
    List<ChatRoomResponseDto> getUnassignedRooms();
    
    // Gán staff cho phòng chat
    ChatRoomResponseDto assignStaffToRoom(String roomId, Integer staffId);
    
    // Đóng phòng chat
    void closeChatRoom(String roomId);
    
    // ========== MESSAGE MANAGEMENT ==========
    
    // Gửi tin nhắn
    ChatMessageResponseDto sendMessage(ChatMessageRequestDto request, Integer senderId, String senderType);
    
    // Lấy tin nhắn của phòng chat
    List<ChatMessageResponseDto> getRoomMessages(String roomId, Integer currentUserId, String currentUserType);
    
    // Lấy tin nhắn với phân trang
    Page<ChatMessageResponseDto> getRoomMessagesPaginated(String roomId, Integer currentUserId, String currentUserType, Pageable pageable);
    
    // Lấy tin nhắn mới từ thời điểm nhất định
    List<ChatMessageResponseDto> getNewMessages(String roomId, LocalDateTime since, Integer currentUserId, String currentUserType);
    
    // Đánh dấu tin nhắn đã đọc
    void markMessagesAsRead(String roomId, Integer currentUserId, String currentUserType);
    
    // Xóa tin nhắn
    void deleteMessage(Integer messageId, Integer currentUserId);
    
    // Tìm kiếm tin nhắn
    List<ChatMessageResponseDto> searchMessages(String roomId, String keyword, Integer currentUserId, String currentUserType);
    
    // ========== STATISTICS ==========
    
    // Đếm tin nhắn chưa đọc
    Long countUnreadMessages(String roomId, Integer currentUserId, String currentUserType);
    
    // Đếm số phòng chat đang mở
    Long countOpenRooms();
    
    // Đếm số phòng chat chưa có staff
    Long countUnassignedRooms();
    
    // ========== UTILITY ==========
    
    // Kiểm tra quyền truy cập phòng chat
    boolean hasAccessToRoom(String roomId, Integer userId, String userType);
    
    // Tạo tin nhắn hệ thống
    ChatMessageResponseDto sendSystemMessage(String roomId, String content);
    
    // ========== AI CHATBOT - TRA CỨU ĐƠN HÀNG ==========
    
    // Xử lý tin nhắn từ AI chatbot
    ChatMessageResponseDto processAIMessage(String roomId, String messageContent, Integer customerId);
    
    // Tra cứu đơn hàng theo mã
    String lookupOrderByCode(String code, Integer customerId);
    
    // Tạo phản hồi AI cho tra cứu đơn hàng
    String generateOrderLookupResponse(Invoice invoice);
}

