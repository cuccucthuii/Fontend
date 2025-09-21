package org.example.cinema_reservation_system.controller.user;

import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.dto.userdto.UserStatisticsDto;
import org.example.cinema_reservation_system.service.user.UserStatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/statistics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserStatisticsController {

    private final UserStatisticsService userStatisticsService;

    /**
     * Lấy thống kê tổng hợp của người dùng hiện tại
     */
    @GetMapping("/overview")
    public ResponseEntity<UserStatisticsDto> getUserStatistics() {
        Integer customerId = getCurrentCustomerId();
        if (customerId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        UserStatisticsDto statistics = userStatisticsService.getUserStatistics(customerId);
        return ResponseEntity.ok(statistics);
    }

    /**
     * Lấy thống kê cơ bản (số vé, phim, đánh giá, bình luận)
     */
    @GetMapping("/basic")
    public ResponseEntity<UserStatisticsDto> getBasicStatistics() {
        Integer customerId = getCurrentCustomerId();
        if (customerId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        UserStatisticsDto statistics = userStatisticsService.getBasicStatistics(customerId);
        return ResponseEntity.ok(statistics);
    }

    /**
     * Lấy thống kê của người dùng khác (cho admin/staff)
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<UserStatisticsDto> getUserStatisticsById(@PathVariable Integer customerId) {
        // TODO: Kiểm tra quyền admin/staff
        UserStatisticsDto statistics = userStatisticsService.getUserStatistics(customerId);
        return ResponseEntity.ok(statistics);
    }

    /**
     * Lấy ID khách hàng từ authentication context
     */
    private Integer getCurrentCustomerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
            // TODO: Implement logic to get customer ID from UserDetails
            // Tạm thời return null, cần implement logic lấy customer ID
            return null;
        }
        return null;
    }
}
