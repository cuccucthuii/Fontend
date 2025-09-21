package org.example.cinema_reservation_system.service.ai;

import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.repository.chat.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AIService {

    private final ChatMessageRepository chatMessageRepository;
    private final OpenAIClient openAIClient;
    private final GeminiClient geminiClient;
    private final MovieRecommendationService movieRecommendationService;

    @Value("${ai.provider:none}")
    private String provider;

    @Value("${ai.temperature:0.4}")
    private double temperature;

    @Value("${ai.maxTokens:512}")
    private int maxTokens;

    public String generateAIResponse(String roomId, String userMessage, String intentHint) throws Exception {
        // Detect complex questions that need intelligent AI processing
        if (isComplexQuestion(userMessage)) {
            return handleComplexQuestion(roomId, userMessage, intentHint);
        }
        
        List<String> history = buildConversationHistory(roomId, userMessage, intentHint);
        switch (provider.toLowerCase()) {
            case "openai":
                return openAIClient.chatCompletion(history, temperature, maxTokens);
            case "gemini":
                return geminiClient.chatCompletion(history, temperature, maxTokens);
            default:
                throw new IllegalStateException("AI provider is not configured");
        }
    }
    
    /**
     * Detect complex questions that require intelligent AI processing
     */
    private boolean isComplexQuestion(String userMessage) {
        String message = userMessage.toLowerCase();
        
        // Complex question patterns
        String[] complexPatterns = {
            "phim nào hay nhất", "phim nào đáng xem", "phim nào tốt nhất",
            "so sánh", "khác nhau", "giống nhau",
            "gợi ý phim", "phim nào phù hợp", "nên xem phim nào",
            "phim nào cho gia đình", "phim nào cho trẻ em", "phim nào cho người lớn",
            "phim nào tuần này", "phim nào tháng này", "phim nào mới nhất",
            "phim nào theo sở thích", "phim nào theo tâm trạng",
            "phim nào có rating cao", "phim nào được đánh giá tốt",
            "phim nào có diễn viên", "phim nào có đạo diễn",
            "phim nào thể loại", "phim nào genre"
        };
        
        for (String pattern : complexPatterns) {
            if (message.contains(pattern)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Handle complex questions with enhanced AI processing
     */
    private String handleComplexQuestion(String roomId, String userMessage, String intentHint) throws Exception {
        // Try to get real data first
        String dataResponse = tryGetRealDataResponse(userMessage);
        if (dataResponse != null) {
            return dataResponse;
        }
        
        // Build enhanced conversation history for complex questions
        List<String> history = buildComplexConversationHistory(roomId, userMessage, intentHint);
        
        // Use higher temperature for more creative responses
        double complexTemperature = Math.min(temperature + 0.2, 0.8);
        int complexMaxTokens = Math.min(maxTokens + 200, 1000);
        
        switch (provider.toLowerCase()) {
            case "openai":
                return openAIClient.chatCompletion(history, complexTemperature, complexMaxTokens);
            case "gemini":
                return geminiClient.chatCompletion(history, complexTemperature, complexMaxTokens);
            default:
                throw new IllegalStateException("AI provider is not configured");
        }
    }
    
    /**
     * Try to get real data response for specific questions
     */
    private String tryGetRealDataResponse(String userMessage) {
        String message = userMessage.toLowerCase();
        
        try {
            // Handle "phim nào hay nhất" / "top phim"
            if (message.contains("phim nào hay nhất") || message.contains("top phim") || 
                message.contains("phim nào đáng xem") || message.contains("phim nào tốt nhất")) {
                return getTopMoviesResponse();
            }
            
            // Handle "phim nào cho gia đình"
            if (message.contains("phim nào cho gia đình") || message.contains("phim gia đình")) {
                return getFamilyMoviesResponse();
            }
            
            // Handle "phim nào mới nhất"
            if (message.contains("phim nào mới nhất") || message.contains("phim mới")) {
                return getNewestMoviesResponse();
            }
            
            // Handle comparison questions
            if (message.contains("so sánh") && message.contains("phim")) {
                return getComparisonResponse(userMessage);
            }
            
        } catch (Exception e) {
            // If real data fails, fall back to AI
            return null;
        }
        
        return null; // No real data available, use AI
    }
    
    private String getTopMoviesResponse() {
        var topMovies = movieRecommendationService.getTopMovies("popular", 3);
        if (topMovies.isEmpty()) {
            return "Hiện tại chưa có phim nào đang chiếu. Vui lòng quay lại sau!";
        }
        
        StringBuilder response = new StringBuilder("🎬 **Top phim đáng xem nhất hiện tại:**\n\n");
        for (int i = 0; i < topMovies.size(); i++) {
            var movie = topMovies.get(i);
            response.append((i + 1)).append(". **").append(movie.getTenPhim()).append("**\n");
            response.append("   📅 Năm: ").append(movie.getNamSanXuat()).append("\n");
            response.append("   ⏱️ Thời lượng: ").append(movie.getThoiLuong()).append(" phút\n");
            response.append("   🎭 Độ tuổi: ").append(movie.getTuoiGioiHan()).append("\n");
            response.append("   🎥 Định dạng: ").append(movie.getDinhDang()).append("\n\n");
        }
        response.append("Bạn muốn xem chi tiết phim nào?");
        return response.toString();
    }
    
    private String getFamilyMoviesResponse() {
        var familyMovies = movieRecommendationService.getMoviesForAudience("family");
        if (familyMovies.isEmpty()) {
            return "Hiện tại chưa có phim nào phù hợp cho gia đình. Vui lòng quay lại sau!";
        }
        
        StringBuilder response = new StringBuilder("👨‍👩‍👧‍👦 **Phim phù hợp cho gia đình:**\n\n");
        for (var movie : familyMovies) {
            response.append("🎬 **").append(movie.getTenPhim()).append("**\n");
            response.append("   📝 ").append(movie.getMoTa() != null ? 
                (movie.getMoTa().length() > 100 ? movie.getMoTa().substring(0, 100) + "..." : movie.getMoTa()) 
                : "Mô tả chưa có").append("\n");
            response.append("   ⏱️ ").append(movie.getThoiLuong()).append(" phút\n");
            response.append("   🎥 ").append(movie.getDinhDang()).append("\n\n");
        }
        response.append("Bạn muốn đặt vé phim nào?");
        return response.toString();
    }
    
    private String getNewestMoviesResponse() {
        var newestMovies = movieRecommendationService.getTopMovies("newest", 3);
        if (newestMovies.isEmpty()) {
            return "Hiện tại chưa có phim mới nào. Vui lòng quay lại sau!";
        }
        
        StringBuilder response = new StringBuilder("🆕 **Phim mới nhất:**\n\n");
        for (var movie : newestMovies) {
            response.append("🎬 **").append(movie.getTenPhim()).append("**\n");
            response.append("   📅 Phát hành: ").append(movie.getNgayPhatHanh()).append("\n");
            response.append("   🎭 Độ tuổi: ").append(movie.getTuoiGioiHan()).append("\n");
            response.append("   🎥 Định dạng: ").append(movie.getDinhDang()).append("\n\n");
        }
        response.append("Bạn muốn xem suất chiếu của phim nào?");
        return response.toString();
    }
    
    private String getComparisonResponse(String userMessage) {
        // Simple comparison logic - in real implementation, you'd parse movie names from the message
        return "Để so sánh phim, bạn có thể cho mình biết tên 2 phim cụ thể không? Ví dụ: 'So sánh phim A và phim B'";
    }
    
    /**
     * Build enhanced conversation history for complex questions
     */
    private List<String> buildComplexConversationHistory(String roomId, String userMessage, String intentHint) {
        var recent = chatMessageRepository.findLastNByRoomId(roomId, 15); // More context for complex questions
        List<String> messages = new ArrayList<>();
        
        // Enhanced system prompt for complex questions
        String complexSystemPrompt = "##SYSTEM_PROMPT## Bạn là trợ lý AI thông minh của DevCinema với khả năng xử lý câu hỏi phức tạp.\n\n" +
                "## KHẢ NĂNG ĐẶC BIỆT:\n" +
                "🧠 **Phân tích thông minh**: Hiểu sâu câu hỏi và ngữ cảnh\n" +
                "🎯 **Gợi ý cá nhân hóa**: Dựa trên sở thích và tình huống\n" +
                "📊 **So sánh chi tiết**: Phân tích ưu/nhược điểm từng phim\n" +
                "💡 **Lời khuyên chuyên nghiệp**: Đưa ra gợi ý phù hợp nhất\n\n" +
                "## CÁCH XỬ LÝ:\n" +
                "1. **Câu hỏi gợi ý phim**: Hỏi thêm về thể loại, tâm trạng, độ tuổi, thời gian\n" +
                "2. **So sánh phim**: Phân tích từng khía cạnh (cốt truyện, diễn xuất, hiệu ứng)\n" +
                "3. **Phim hay nhất**: Đưa ra top phim theo tiêu chí cụ thể\n" +
                "4. **Phim phù hợp**: Lọc theo đối tượng và hoàn cảnh\n" +
                "5. **Phim mới/tuần này**: Cập nhật thông tin mới nhất\n\n" +
                "## KIẾN THỨC PHIM:\n" +
                "- Thể loại: Hành động, Tình cảm, Hài, Kinh dị, Khoa học viễn tưởng, Hoạt hình, Gia đình\n" +
                "- Độ tuổi: P (mọi lứa tuổi), T13 (13+), T16 (16+), T18 (18+)\n" +
                "- Định dạng: 2D, 3D, 4DX, IMAX\n" +
                "- Thời gian: Sáng (8h-12h), Chiều (12h-18h), Tối (18h-24h)\n\n" +
                "Trả lời chi tiết, thân thiện và hữu ích. Khi thiếu thông tin cụ thể, hãy đưa ra gợi ý dựa trên kiến thức chung và hỏi thêm để cá nhân hóa.";
        
        messages.add(complexSystemPrompt);
        
        if (intentHint != null && !intentHint.isBlank()) {
            messages.add("[INTENT] " + intentHint);
        }
        
        // Add more conversation context for complex questions
        for (int i = Math.max(0, recent.size() - 15); i < recent.size(); i++) {
            var m = recent.get(i);
            String rolePrefix = "SYSTEM".equals(m.getSenderType()) ? "assistant" :
                    ("CUSTOMER".equals(m.getSenderType()) ? "user" : "assistant");
            messages.add(m.getSenderName() + ": " + m.getMessageContent());
        }
        
        messages.add("[COMPLEX_QUESTION] " + userMessage);
        return messages;
    }

    private List<String> buildConversationHistory(String roomId, String userMessage, String intentHint) {
        var recent = chatMessageRepository.findLastNByRoomId(roomId, 10);
        List<String> messages = new ArrayList<>();
        
        // Enhanced system prompt with intelligent features
        String systemPrompt = "##SYSTEM_PROMPT## Bạn là trợ lý AI thông minh của DevCinema. Trả lời bằng tiếng Việt, thân thiện và chuyên nghiệp.\n\n" +
                "## KHẢ NĂNG THÔNG MINH:\n" +
                "✅ Hiểu ngữ cảnh cuộc hội thoại\n" +
                "✅ Xử lý câu hỏi phức tạp và mới\n" +
                "✅ Gợi ý phim theo sở thích cá nhân\n" +
                "✅ So sánh và phân tích phim\n" +
                "✅ Đưa ra lời khuyên phù hợp\n\n" +
                "## KIẾN THỨC CƠ BẢN:\n" +
                "1) Thông tin phim: 'Hôm nay rạp đang chiếu: [danh_sách]'; 'Có phim hoạt hình: [danh_sách]'; 'Phim [X] còn suất, bạn muốn ngày nào?'\n" +
                "2) Suất chiếu: 'Ngày [ngày], [phim] có các suất: [giờ_chiếu]'; 'Có suất sau 22h tuỳ phim'\n" +
                "3) Giá vé & KM: 'Người lớn: [giá], trẻ em: [giá] (tuỳ suất)'; 'Thứ 3: 2D đồng giá [giá]'; '4DX từ [giá]–[giá]'\n" +
                "4) Đặt vé & thanh toán: 'Chọn phim→suất→ghế→thanh toán (hỗ trợ VNPAY/MoMo/ZaloPay/thẻ)'\n" +
                "5) Chính sách: 'Không hoàn/hủy online'; 'Không trả vé tại quầy'; 'Đi muộn vẫn vào nhưng lỡ phần đầu'\n" +
                "6) Dịch vụ: 'Combo bắp+nước từ [giá]; bắp từ [giá], nước từ [giá]'\n" +
                "7) Tài khoản: 'Quên mật khẩu: dùng chức năng quên mật khẩu'; 'Điểm thành viên: hiện chưa triển khai'\n\n" +
                "## XỬ LÝ CÂU HỎI PHỨC TẠP:\n" +
                "- 'Phim nào hay nhất?': Gợi ý theo thể loại, độ tuổi, thời gian\n" +
                "- 'So sánh 2 phim': Phân tích ưu/nhược điểm, phù hợp với ai\n" +
                "- 'Gợi ý phim theo sở thích': Hỏi thêm về thể loại, tâm trạng, độ tuổi\n" +
                "- 'Phim nào phù hợp cho gia đình?': Lọc theo tuổi giới hạn P, T13\n" +
                "- 'Phim nào đáng xem nhất tuần này?': Dựa trên rating, lượt xem, đánh giá\n\n" +
                "Khi người dùng yêu cầu hỗ trợ thêm, hãy đề xuất 'Mình sẽ chuyển yêu cầu cho nhân viên hỗ trợ'.";
        
        messages.add(systemPrompt);
        if (intentHint != null && !intentHint.isBlank()) {
            messages.add("[INTENT] " + intentHint);
        }
        for (int i = Math.max(0, recent.size() - 10); i < recent.size(); i++) {
            var m = recent.get(i);
            String rolePrefix = "SYSTEM".equals(m.getSenderType()) ? "assistant" :
                    ("CUSTOMER".equals(m.getSenderType()) ? "user" : "assistant");
            messages.add(m.getSenderName() + ": " + m.getMessageContent());
        }
        messages.add(userMessage);
        return messages;
    }
}


