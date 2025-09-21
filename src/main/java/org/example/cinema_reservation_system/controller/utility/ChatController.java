package org.example.cinema_reservation_system.controller.utility;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.dto.chatdto.*;
import org.example.cinema_reservation_system.service.utility.ChatService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ChatController {

    private final ChatService chatService;

    // Tạo phòng chat mới (chỉ customer)
    @PostMapping("/rooms")
    public ResponseEntity<ChatRoomResponseDto> createChatRoom(@Valid @RequestBody ChatRoomRequestDto request) {
        ChatRoomResponseDto response = chatService.createChatRoom(request);
        return ResponseEntity.ok(response);
    }

    // Lấy thông tin phòng chat
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<ChatRoomResponseDto> getChatRoom(@PathVariable String roomId) {
        ChatRoomResponseDto response = chatService.getChatRoom(roomId);
        return ResponseEntity.ok(response);
    }

    // Lấy danh sách phòng chat của customer
    @GetMapping("/customer/rooms")
    public ResponseEntity<List<ChatRoomResponseDto>> getCustomerRooms(@RequestParam Integer customerId) {
        List<ChatRoomResponseDto> response = chatService.getCustomerRooms(customerId);
        return ResponseEntity.ok(response);
    }

    // Lấy danh sách phòng chat của staff
    @GetMapping("/staff/rooms")
    public ResponseEntity<List<ChatRoomResponseDto>> getStaffRooms(@RequestParam Integer staffId) {
        List<ChatRoomResponseDto> response = chatService.getStaffRooms(staffId);
        return ResponseEntity.ok(response);
    }

    // Lấy danh sách phòng chat đang mở (cho admin/staff)
    @GetMapping("/admin/rooms")
    public ResponseEntity<Page<ChatRoomResponseDto>> getOpenRooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ChatRoomResponseDto> response = chatService.getOpenRooms(pageable);
        return ResponseEntity.ok(response);
    }

    // Lấy danh sách phòng chat chưa có staff
    @GetMapping("/admin/unassigned-rooms")
    public ResponseEntity<List<ChatRoomResponseDto>> getUnassignedRooms() {
        List<ChatRoomResponseDto> response = chatService.getUnassignedRooms();
        return ResponseEntity.ok(response);
    }

    // Gán staff cho phòng chat (admin)
    @PostMapping("/admin/rooms/{roomId}/assign")
    public ResponseEntity<ChatRoomResponseDto> assignStaffToRoom(
            @PathVariable String roomId,
            @RequestParam Integer staffId) {
        ChatRoomResponseDto response = chatService.assignStaffToRoom(roomId, staffId);
        return ResponseEntity.ok(response);
    }

    // Đóng phòng chat
    @PostMapping("/rooms/{roomId}/close")
    public ResponseEntity<String> closeChatRoom(@PathVariable String roomId) {
        chatService.closeChatRoom(roomId);
        return ResponseEntity.ok("Phòng chat đã được đóng");
    }

    // Gửi tin nhắn
    @PostMapping("/messages")
    public ResponseEntity<ChatMessageResponseDto> sendMessage(@Valid @RequestBody ChatMessageRequestDto request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer senderId = getCurrentUserId(auth);
        String senderType = getCurrentUserType(auth);
        
        ChatMessageResponseDto response = chatService.sendMessage(request, senderId, senderType);
        return ResponseEntity.ok(response);
    }

    // Lấy tin nhắn của phòng chat
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessageResponseDto>> getRoomMessages(@PathVariable String roomId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer currentUserId = getCurrentUserId(auth);
        String currentUserType = getCurrentUserType(auth);
        
        List<ChatMessageResponseDto> response = chatService.getRoomMessages(roomId, currentUserId, currentUserType);
        return ResponseEntity.ok(response);
    }

    // Lấy tin nhắn với phân trang
    @GetMapping("/rooms/{roomId}/messages/paginated")
    public ResponseEntity<Page<ChatMessageResponseDto>> getRoomMessagesPaginated(
            @PathVariable String roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer currentUserId = getCurrentUserId(auth);
        String currentUserType = getCurrentUserType(auth);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ChatMessageResponseDto> response = chatService.getRoomMessagesPaginated(roomId, currentUserId, currentUserType, pageable);
        return ResponseEntity.ok(response);
    }

    // Lấy tin nhắn mới từ thời điểm nhất định
    @GetMapping("/rooms/{roomId}/messages/new")
    public ResponseEntity<List<ChatMessageResponseDto>> getNewMessages(
            @PathVariable String roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer currentUserId = getCurrentUserId(auth);
        String currentUserType = getCurrentUserType(auth);
        
        List<ChatMessageResponseDto> response = chatService.getNewMessages(roomId, since, currentUserId, currentUserType);
        return ResponseEntity.ok(response);
    }

    // Đánh dấu tin nhắn đã đọc
    @PostMapping("/rooms/{roomId}/messages/read")
    public ResponseEntity<String> markMessagesAsRead(@PathVariable String roomId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer currentUserId = getCurrentUserId(auth);
        String currentUserType = getCurrentUserType(auth);
        
        chatService.markMessagesAsRead(roomId, currentUserId, currentUserType);
        return ResponseEntity.ok("Đã đánh dấu tin nhắn đã đọc");
    }

    // Xóa tin nhắn
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<String> deleteMessage(@PathVariable Integer messageId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer currentUserId = getCurrentUserId(auth);
        
        chatService.deleteMessage(messageId, currentUserId);
        return ResponseEntity.ok("Đã xóa tin nhắn");
    }

    // Tìm kiếm tin nhắn
    @GetMapping("/rooms/{roomId}/messages/search")
    public ResponseEntity<List<ChatMessageResponseDto>> searchMessages(
            @PathVariable String roomId,
            @RequestParam String keyword) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer currentUserId = getCurrentUserId(auth);
        String currentUserType = getCurrentUserType(auth);
        
        List<ChatMessageResponseDto> response = chatService.searchMessages(roomId, keyword, currentUserId, currentUserType);
        return ResponseEntity.ok(response);
    }

    // ========== STATISTICS ==========

    // Đếm tin nhắn chưa đọc
    @GetMapping("/rooms/{roomId}/unread-count")
    public ResponseEntity<Long> countUnreadMessages(@PathVariable String roomId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer currentUserId = getCurrentUserId(auth);
        String currentUserType = getCurrentUserType(auth);
        
        Long count = chatService.countUnreadMessages(roomId, currentUserId, currentUserType);
        return ResponseEntity.ok(count);
    }

    // Đếm số phòng chat đang mở
    @GetMapping("/admin/stats/open-rooms")
    public ResponseEntity<Long> countOpenRooms() {
        Long count = chatService.countOpenRooms();
        return ResponseEntity.ok(count);
    }

    // Đếm số phòng chat chưa có staff
    @GetMapping("/admin/stats/unassigned-rooms")
    public ResponseEntity<Long> countUnassignedRooms() {
        Long count = chatService.countUnassignedRooms();
        return ResponseEntity.ok(count);
    }

    // ========== UTILITY METHODS ==========

    private Integer getCurrentUserId(Authentication auth) {
        if (auth != null && auth.getPrincipal() instanceof org.example.cinema_reservation_system.entity.UserAccount) {
            return ((org.example.cinema_reservation_system.entity.UserAccount) auth.getPrincipal()).getIdTaiKhoan();
        }
        return null;
    }

    private String getCurrentUserType(Authentication auth) {
        if (auth != null && auth.getAuthorities() != null) {
            String role = auth.getAuthorities().iterator().next().getAuthority();
            if (role.contains("ADMIN")) return "ADMIN";
            if (role.contains("STAFF")) return "STAFF";
            if (role.contains("CLIENT")) return "CUSTOMER";
        }
        return "CUSTOMER"; // Default
    }
    
    // ========== AI CHATBOT - TRA CỨU ĐƠN HÀNG ==========
    
    // Xử lý tin nhắn AI chatbot
    @PostMapping("/ai/process")
    public ResponseEntity<ChatMessageResponseDto> processAIMessage(
            @RequestParam String roomId,
            @RequestParam String messageContent,
            @RequestParam Integer customerId) {
        
        ChatMessageResponseDto response = chatService.processAIMessage(roomId, messageContent, customerId);
        return ResponseEntity.ok(response);
    }
    
    // Streaming SSE: typing indicator + final answer (SseEmitter - Spring MVC)
    @GetMapping(path = "/ai/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamAI(@RequestParam String roomId,
                               @RequestParam String messageContent,
                               @RequestParam Integer customerId) {
        SseEmitter emitter = new SseEmitter(30000L);
        java.util.concurrent.Executors.newSingleThreadExecutor().submit(() -> {
            try {
                for (int i = 0; i < 4; i++) {
                    emitter.send(SseEmitter.event().name("typing").data("typing"));
                    Thread.sleep(500);
                }
                ChatMessageResponseDto resp = chatService.processAIMessage(roomId, messageContent, customerId);
                emitter.send(SseEmitter.event().name("message").data(resp.getMessageContent()));
                emitter.complete();
            } catch (Exception e) {
                try { emitter.completeWithError(e); } catch (Exception ignored) {}
            }
        });
        return emitter;
    }

    // Tra cứu đơn hàng trực tiếp
    @GetMapping("/ai/lookup-order")
    public ResponseEntity<String> lookupOrder(
            @RequestParam String code,
            @RequestParam Integer customerId) {
        
        String result = chatService.lookupOrderByCode(code, customerId);
        return ResponseEntity.ok(result);
    }
}

