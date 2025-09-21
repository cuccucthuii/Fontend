package org.example.cinema_reservation_system.service.utility;

import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.dto.chatdto.*;
import org.example.cinema_reservation_system.entity.ChatMessage;
import org.example.cinema_reservation_system.entity.ChatRoom;
import org.example.cinema_reservation_system.entity.Customer;
import org.example.cinema_reservation_system.entity.Staff;
import org.example.cinema_reservation_system.exception.BadRequestException;
import org.example.cinema_reservation_system.exception.ResourceNotFoundException;
import org.example.cinema_reservation_system.repository.chat.ChatMessageRepository;
import org.example.cinema_reservation_system.repository.chat.ChatRoomRepository;
import org.example.cinema_reservation_system.repository.customer.CustomerRepository;
import org.example.cinema_reservation_system.repository.staff.StaffRepository;
import org.example.cinema_reservation_system.service.utility.ChatService;
import org.example.cinema_reservation_system.service.ai.AIService;
import org.example.cinema_reservation_system.utils.enums.MessageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.example.cinema_reservation_system.entity.Invoice;
import org.example.cinema_reservation_system.repository.invoice.InvoiceRepository;
import org.example.cinema_reservation_system.utils.enums.TrangThaiHoaDon;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatServiceImpl implements ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final CustomerRepository customerRepository;
    private final StaffRepository staffRepository;
    private final InvoiceRepository invoiceRepository;
    private final AIService aiService;

    // ========== CHAT ROOM MANAGEMENT ==========

    @Override
    public ChatRoomResponseDto createChatRoom(ChatRoomRequestDto request) {
        // Kiểm tra customer tồn tại
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng"));

        // Kiểm tra xem customer đã có phòng chat đang mở chưa
        chatRoomRepository.findOpenRoomByCustomerId(request.getCustomerId())
                .ifPresent(room -> {
                    throw new BadRequestException("Khách hàng đã có phòng chat đang mở");
                });

        // Tạo phòng chat mới
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setRoomId(generateRoomId());
        chatRoom.setRoomName(request.getRoomName() != null ? request.getRoomName() : 
                "Hỗ trợ khách hàng - " + customer.getTenKhachHang());
        chatRoom.setRoomType("CUSTOMER_SUPPORT");
        chatRoom.setCustomer(customer);
        chatRoom.setStatus("OPEN");
        chatRoom.setPriority("NORMAL");
        chatRoom.setTags(request.getTags());
        chatRoom.setIsActive(true);

        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);

        // Gửi tin nhắn chào mừng
        sendSystemMessage(savedRoom.getRoomId(), 
                "Chào mừng bạn đến với hệ thống hỗ trợ khách hàng! Chúng tôi sẽ phản hồi trong thời gian sớm nhất.");

        return convertToRoomDto(savedRoom);
    }

    @Override
    public ChatRoomResponseDto getChatRoom(String roomId) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng chat"));
        return convertToRoomDto(chatRoom);
    }

    @Override
    public List<ChatRoomResponseDto> getCustomerRooms(Integer customerId) {
        List<ChatRoom> rooms = chatRoomRepository.findByCustomerId(customerId);
        return rooms.stream()
                .map(this::convertToRoomDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatRoomResponseDto> getStaffRooms(Integer staffId) {
        List<ChatRoom> rooms = chatRoomRepository.findByStaffId(staffId);
        return rooms.stream()
                .map(this::convertToRoomDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ChatRoomResponseDto> getOpenRooms(Pageable pageable) {
        Page<ChatRoom> rooms = chatRoomRepository.findOpenRooms(pageable);
        return rooms.map(this::convertToRoomDto);
    }

    @Override
    public List<ChatRoomResponseDto> getUnassignedRooms() {
        List<ChatRoom> rooms = chatRoomRepository.findUnassignedRooms();
        return rooms.stream()
                .map(this::convertToRoomDto)
                .collect(Collectors.toList());
    }

    @Override
    public ChatRoomResponseDto assignStaffToRoom(String roomId, Integer staffId) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng chat"));

        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên"));

        chatRoomRepository.assignStaffToRoom(roomId, staffId);

        // Gửi tin nhắn thông báo
        sendSystemMessage(roomId, "Nhân viên " + staff.getTenNhanVien() + " đã được phân công hỗ trợ bạn.");

        return getChatRoom(roomId);
    }

    @Override
    public void closeChatRoom(String roomId) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng chat"));

        chatRoomRepository.closeRoom(roomId, LocalDateTime.now());

        // Gửi tin nhắn thông báo đóng phòng
        sendSystemMessage(roomId, "Phòng chat đã được đóng. Cảm ơn bạn đã sử dụng dịch vụ hỗ trợ!");
    }

    // ========== MESSAGE MANAGEMENT ==========

    @Override
    public ChatMessageResponseDto sendMessage(ChatMessageRequestDto request, Integer senderId, String senderType) {
        // Kiểm tra phòng chat tồn tại
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng chat"));

        // Kiểm tra quyền truy cập
        if (!hasAccessToRoom(request.getRoomId(), senderId, senderType)) {
            throw new BadRequestException("Bạn không có quyền truy cập phòng chat này");
        }

        // Tạo tin nhắn mới
        ChatMessage message = new ChatMessage();
        message.setRoomId(request.getRoomId());
        message.setMessageContent(request.getMessageContent());
        message.setMessageType(request.getMessageType());
        message.setFileUrl(request.getFileUrl());
        message.setFileName(request.getFileName());
        message.setFileSize(request.getFileSize());
        message.setReplyToMessageId(request.getReplyToMessageId());
        message.setSenderType(senderType);

        // Set sender name dựa trên sender type
        String senderName = getSenderName(senderId, senderType);
        message.setSenderName(senderName);

        ChatMessage savedMessage = chatMessageRepository.save(message);

        // Cập nhật thời gian tin nhắn cuối của phòng chat
        chatRoomRepository.updateLastMessageAt(request.getRoomId(), LocalDateTime.now());

        // Xử lý AI chatbot nếu là tin nhắn từ khách hàng
        if ("CUSTOMER".equals(senderType) && chatRoom.getCustomer() != null) {
            try {
                processAIMessage(request.getRoomId(), request.getMessageContent(), chatRoom.getCustomer().getIdKhachHang());
            } catch (Exception e) {
                System.err.println("Lỗi xử lý AI chatbot: " + e.getMessage());
            }
        }

        return convertToMessageDto(savedMessage, senderId, senderType);
    }

    @Override
    public List<ChatMessageResponseDto> getRoomMessages(String roomId, Integer currentUserId, String currentUserType) {
        if (!hasAccessToRoom(roomId, currentUserId, currentUserType)) {
            throw new BadRequestException("Bạn không có quyền truy cập phòng chat này");
        }

        List<ChatMessage> messages = chatMessageRepository.findByRoomIdOrderByCreatedAtAsc(roomId);
        return messages.stream()
                .map(message -> convertToMessageDto(message, currentUserId, currentUserType))
                .collect(Collectors.toList());
    }

    @Override
    public Page<ChatMessageResponseDto> getRoomMessagesPaginated(String roomId, Integer currentUserId, String currentUserType, Pageable pageable) {
        if (!hasAccessToRoom(roomId, currentUserId, currentUserType)) {
            throw new BadRequestException("Bạn không có quyền truy cập phòng chat này");
        }

        Page<ChatMessage> messages = chatMessageRepository.findByRoomIdOrderByCreatedAtDesc(roomId, pageable);
        return messages.map(message -> convertToMessageDto(message, currentUserId, currentUserType));
    }

    @Override
    public List<ChatMessageResponseDto> getNewMessages(String roomId, LocalDateTime since, Integer currentUserId, String currentUserType) {
        if (!hasAccessToRoom(roomId, currentUserId, currentUserType)) {
            throw new BadRequestException("Bạn không có quyền truy cập phòng chat này");
        }

        List<ChatMessage> messages = chatMessageRepository.findNewMessagesSince(roomId, since);
        return messages.stream()
                .map(message -> convertToMessageDto(message, currentUserId, currentUserType))
                .collect(Collectors.toList());
    }

    @Override
    public void markMessagesAsRead(String roomId, Integer currentUserId, String currentUserType) {
        if (!hasAccessToRoom(roomId, currentUserId, currentUserType)) {
            throw new BadRequestException("Bạn không có quyền truy cập phòng chat này");
        }

        chatMessageRepository.markMessagesAsRead(roomId, currentUserType, LocalDateTime.now());
    }

    @Override
    public void deleteMessage(Integer messageId, Integer currentUserId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tin nhắn"));

        // Chỉ cho phép xóa tin nhắn của chính mình
        if (!message.getSender().getIdTaiKhoan().equals(currentUserId)) {
            throw new BadRequestException("Bạn chỉ có thể xóa tin nhắn của chính mình");
        }

        chatMessageRepository.softDeleteMessage(messageId);
    }

    @Override
    public List<ChatMessageResponseDto> searchMessages(String roomId, String keyword, Integer currentUserId, String currentUserType) {
        if (!hasAccessToRoom(roomId, currentUserId, currentUserType)) {
            throw new BadRequestException("Bạn không có quyền truy cập phòng chat này");
        }

        List<ChatMessage> messages = chatMessageRepository.searchMessagesByContent(roomId, keyword);
        return messages.stream()
                .map(message -> convertToMessageDto(message, currentUserId, currentUserType))
                .collect(Collectors.toList());
    }

    // ========== STATISTICS ==========

    @Override
    public Long countUnreadMessages(String roomId, Integer currentUserId, String currentUserType) {
        return chatMessageRepository.countUnreadMessagesByRoomIdAndNotSenderType(roomId, currentUserType);
    }

    @Override
    public Long countOpenRooms() {
        return chatRoomRepository.countOpenRooms();
    }

    @Override
    public Long countUnassignedRooms() {
        return chatRoomRepository.countUnassignedRooms();
    }

    // ========== UTILITY ==========

    @Override
    public boolean hasAccessToRoom(String roomId, Integer userId, String userType) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId).orElse(null);
        if (chatRoom == null) return false;

        switch (userType) {
            case "CUSTOMER":
                return chatRoom.getCustomer().getIdKhachHang().equals(userId);
            case "STAFF":
            case "ADMIN":
                return chatRoom.getStaff() == null || chatRoom.getStaff().getIdNhanVien().equals(userId);
            default:
                return false;
        }
    }

    @Override
    public ChatMessageResponseDto sendSystemMessage(String roomId, String content) {
        ChatMessage systemMessage = new ChatMessage();
        systemMessage.setRoomId(roomId);
        systemMessage.setMessageContent(content);
        systemMessage.setMessageType(MessageType.SYSTEM);
        systemMessage.setSenderType("SYSTEM");
        systemMessage.setSenderName("Hệ thống");
        systemMessage.setIsRead(true);

        ChatMessage savedMessage = chatMessageRepository.save(systemMessage);
        return convertToMessageDto(savedMessage, null, "SYSTEM");
    }

    // ========== AI CHATBOT - TRA CỨU ĐƠN HÀNG ==========
    
    @Override
    public ChatMessageResponseDto processAIMessage(String roomId, String messageContent, Integer customerId) {
        // Phân tích intent
        String intent = analyzeIntent(messageContent);

        // Thử gọi LLM thật nếu được cấu hình; nếu lỗi thì fallback rule-based
        try {
            String llmResponse = aiService.generateAIResponse(roomId, messageContent, intent);
            return sendSystemMessage(roomId, llmResponse);
        } catch (Exception ex) {
            String fallback;
            switch (intent) {
                case "ORDER_LOOKUP":
                    String orderCode = extractOrderCode(messageContent);
                    fallback = lookupOrderByCode(orderCode, customerId);
                    break;
                case "GREETING":
                    fallback = "Xin chào! Tôi là trợ lý ảo của DevCinema. Tôi có thể giúp bạn:\n" +
                            "• Tra cứu đơn hàng bằng mã giao dịch hoặc mã đặt vé\n" +
                            "• Hỗ trợ thông tin về phim, suất chiếu\n" +
                            "• Giải đáp thắc mắc về dịch vụ\n" +
                            "Bạn cần hỗ trợ gì?";
                    break;
                case "HELP":
                    fallback = "Tôi có thể giúp bạn:\n" +
                            "🔍 **Tra cứu đơn hàng**: Gửi mã giao dịch (TXN...) hoặc mã đặt vé (16 số)\n" +
                            "📽️ **Thông tin phim**: Hỏi về phim, suất chiếu, giá vé\n" +
                            "🎫 **Đặt vé**: Hướng dẫn đặt vé online\n" +
                            "💰 **Thanh toán**: Thông tin về các phương thức thanh toán\n" +
                            "❓ **Khác**: Nếu cần hỗ trợ khác, nhân viên sẽ phản hồi sớm nhất";
                    break;
                default:
                    fallback = "Cảm ơn bạn đã liên hệ! Nhân viên hỗ trợ sẽ phản hồi trong thời gian sớm nhất. " +
                            "Trong khi chờ đợi, bạn có thể tra cứu đơn hàng bằng cách gửi mã giao dịch hoặc mã đặt vé.";
            }
            return sendSystemMessage(roomId, fallback);
        }
    }
    
    @Override
    public String lookupOrderByCode(String code, Integer customerId) {
        if (code == null || code.trim().isEmpty()) {
            return "❌ Vui lòng cung cấp mã giao dịch hoặc mã đặt vé để tra cứu.";
        }
        
        // Tìm hóa đơn theo mã
        Invoice invoice = invoiceRepository.findByCustomerAndMa(customerId, code.trim())
                .orElse(null);
        
        if (invoice == null) {
            return "❌ Không tìm thấy đơn hàng với mã: " + code + "\n" +
                    "Vui lòng kiểm tra lại mã hoặc liên hệ nhân viên hỗ trợ.";
        }
        
        return generateOrderLookupResponse(invoice);
    }
    
    @Override
    public String generateOrderLookupResponse(Invoice invoice) {
        StringBuilder response = new StringBuilder();
        response.append("✅ **Tìm thấy đơn hàng!**\n\n");
        
        // Thông tin cơ bản
        response.append("📋 **Thông tin đơn hàng:**\n");
        response.append("• Mã giao dịch: `").append(invoice.getMaGiaoDich()).append("`\n");
        response.append("• Mã đặt vé: `").append(invoice.getMaDatVe()).append("`\n");
        response.append("• Ngày đặt: ").append(invoice.getNgayDat().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n");
        response.append("• Tổng tiền: ").append(String.format("%,.0f", invoice.getTongTien())).append(" ₫\n");
        response.append("• Trạng thái: ").append(getStatusText(invoice.getTrangThai())).append("\n");
        
        // Thông tin khách hàng
        response.append("\n👤 **Thông tin khách hàng:**\n");
        response.append("• Họ tên: ").append(invoice.getTenKhachHang()).append("\n");
        response.append("• Số điện thoại: ").append(invoice.getSoDienThoai()).append("\n");
        
        // Thông tin thanh toán
        if (invoice.getPhuongThucThanhToan() != null) {
            response.append("• Phương thức thanh toán: ").append(invoice.getPhuongThucThanhToan()).append("\n");
        }
        
        // Thông tin email
        if (invoice.getTrangThaiEmail() != null) {
            response.append("• Trạng thái email: ").append(getEmailStatusText(invoice.getTrangThaiEmail())).append("\n");
        }
        
        // Ghi chú
        if (invoice.getGhiChu() != null && !invoice.getGhiChu().trim().isEmpty()) {
            response.append("\n📝 **Ghi chú:**\n").append(invoice.getGhiChu()).append("\n");
        }
        
        // Hướng dẫn
        response.append("\n💡 **Hướng dẫn:**\n");
        response.append("• Đem mã đặt vé đến quầy giao dịch để nhận vé\n");
        response.append("• Đến rạp trước giờ chiếu 15 phút\n");
        response.append("• Xuất trình giấy tờ tùy thân khi cần thiết\n");
        
        return response.toString();
    }
    
    // ========== PRIVATE HELPER METHODS ==========
    
    private String analyzeIntent(String message) {
        String lowerMessage = message.toLowerCase();
        
        // Tra cứu đơn hàng
        if (lowerMessage.contains("tra cứu") || lowerMessage.contains("tìm") || 
            lowerMessage.contains("mã") || lowerMessage.contains("đơn hàng") ||
            lowerMessage.matches(".*TXN\\d+.*") || lowerMessage.matches(".*\\d{16}.*")) {
            return "ORDER_LOOKUP";
        }
        
        // Chào hỏi
        if (lowerMessage.contains("xin chào") || lowerMessage.contains("hello") || 
            lowerMessage.contains("hi") || lowerMessage.contains("chào")) {
            return "GREETING";
        }
        
        // Hỗ trợ
        if (lowerMessage.contains("giúp") || lowerMessage.contains("hỗ trợ") || 
            lowerMessage.contains("làm gì") || lowerMessage.contains("có thể")) {
            return "HELP";
        }
        
        return "UNKNOWN";
    }
    
    private String extractOrderCode(String message) {
        // Tìm mã giao dịch TXN
        if (message.matches(".*TXN\\d+.*")) {
            return message.replaceAll(".*(TXN\\d+).*", "$1");
        }
        
        // Tìm mã đặt vé 16 số
        if (message.matches(".*\\d{16}.*")) {
            return message.replaceAll(".*(\\d{16}).*", "$1");
        }
        
        // Tìm số có 10-20 chữ số (có thể là mã)
        if (message.matches(".*\\d{10,20}.*")) {
            return message.replaceAll(".*(\\d{10,20}).*", "$1");
        }
        
        return null;
    }
    
    private String getStatusText(TrangThaiHoaDon status) {
        switch (status) {
            case DA_THANH_TOAN:
                return "✅ Đã thanh toán";
            case CHO_THANH_TOAN:
                return "⏳ Chờ thanh toán";
            case DA_HUY:
                return "❌ Đã hủy";
            default:
                return status.toString();
        }
    }
    
    private String getEmailStatusText(String emailStatus) {
        switch (emailStatus) {
            case "DA_GUI":
                return "✅ Đã gửi email";
            case "CHUA_GUI":
                return "⏳ Chưa gửi email";
            case "LOI_GUI":
                return "❌ Lỗi gửi email";
            default:
                return emailStatus;
        }
    }

    // ========== PRIVATE METHODS ==========

    private String generateRoomId() {
        return "ROOM_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String getSenderName(Integer senderId, String senderType) {
        switch (senderType) {
            case "CUSTOMER":
                Customer customer = customerRepository.findById(senderId)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng"));
                return customer.getTenKhachHang();
            case "STAFF":
            case "ADMIN":
                Staff staff = staffRepository.findById(senderId)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên"));
                return staff.getTenNhanVien();
            default:
                return "Unknown";
        }
    }

    private ChatRoomResponseDto convertToRoomDto(ChatRoom chatRoom) {
        ChatRoomResponseDto dto = new ChatRoomResponseDto();
        dto.setIdRoom(chatRoom.getIdRoom());
        dto.setRoomId(chatRoom.getRoomId());
        dto.setRoomName(chatRoom.getRoomName());
        dto.setRoomType(chatRoom.getRoomType());
        dto.setIsActive(chatRoom.getIsActive());
        dto.setLastMessageAt(chatRoom.getLastMessageAt());
        dto.setCreatedAt(chatRoom.getCreatedAt());
        dto.setClosedAt(chatRoom.getClosedAt());
        dto.setStatus(chatRoom.getStatus());
        dto.setPriority(chatRoom.getPriority());
        dto.setTags(chatRoom.getTags());

        if (chatRoom.getCustomer() != null) {
            dto.setCustomerId(chatRoom.getCustomer().getIdKhachHang());
            dto.setCustomerName(chatRoom.getCustomer().getTenKhachHang());
            dto.setCustomerEmail(chatRoom.getCustomer().getEmail());
        }

        if (chatRoom.getStaff() != null) {
            dto.setStaffId(chatRoom.getStaff().getIdNhanVien());
            dto.setStaffName(chatRoom.getStaff().getTenNhanVien());
        }

        // Đếm tin nhắn chưa đọc
        dto.setUnreadCount(chatMessageRepository.countUnreadMessagesByRoomId(chatRoom.getRoomId()).intValue());

        // Lấy tin nhắn cuối cùng
        List<ChatMessage> lastMessages = chatMessageRepository.findLastMessageByRoomId(chatRoom.getRoomId(), 
                org.springframework.data.domain.PageRequest.of(0, 1));
        if (!lastMessages.isEmpty()) {
            dto.setLastMessage(lastMessages.get(0).getMessageContent());
        }

        return dto;
    }

    private ChatMessageResponseDto convertToMessageDto(ChatMessage message, Integer currentUserId, String currentUserType) {
        ChatMessageResponseDto dto = new ChatMessageResponseDto();
        dto.setIdMessage(message.getIdMessage());
        dto.setRoomId(message.getRoomId());
        dto.setSenderName(message.getSenderName());
        dto.setSenderType(message.getSenderType());
        dto.setMessageContent(message.getMessageContent());
        dto.setMessageType(message.getMessageType());
        dto.setFileUrl(message.getFileUrl());
        dto.setFileName(message.getFileName());
        dto.setFileSize(message.getFileSize());
        dto.setIsRead(message.getIsRead());
        dto.setCreatedAt(message.getCreatedAt());
        dto.setReadAt(message.getReadAt());
        dto.setReplyToMessageId(message.getReplyToMessageId());
        dto.setMessageStatus(message.getMessageStatus());

        // Kiểm tra có phải tin nhắn của mình không
        dto.setIsOwnMessage(currentUserId != null && 
                message.getSender() != null && 
                message.getSender().getIdTaiKhoan().equals(currentUserId));

        return dto;
    }
}

