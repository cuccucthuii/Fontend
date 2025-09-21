package org.example.cinema_reservation_system.controller.policy;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/policy")
@CrossOrigin(origins = "*")
public class NoRefundPolicyController {
    
    /**
     * Lấy thông tin chính sách No Refund của Beta Cinemas
     * GET /api/policy/no-refund
     */
    @GetMapping("/no-refund")
    public ResponseEntity<Map<String, Object>> getNoRefundPolicy() {
        return ResponseEntity.ok(Map.of(
            "policyName", "Beta Cinemas No Refund Policy",
            "description", "Beta Cinemas áp dụng chính sách KHÔNG HOÀN/HỦY VÉ",
            "details", Map.of(
                "noCancellation", "Không cho phép hủy vé sau khi thanh toán",
                "noRefund", "Không hoàn tiền trong mọi trường hợp",
                "noExchange", "Không đổi vé sang suất chiếu khác",
                "customerResponsibility", "Khách hàng có trách nhiệm kiểm tra kỹ thông tin trước khi thanh toán",
                "validReasons", "Chỉ hoàn tiền trong trường hợp lỗi kỹ thuật từ phía hệ thống"
            ),
            "warning", "VUI LÒNG KIỂM TRA KỸ THÔNG TIN TRƯỚC KHI THANH TOÁN",
            "effectiveDate", "2024-01-01",
            "lastUpdated", "2024-12-01"
        ));
    }
    
    /**
     * Xác nhận khách hàng đã đọc và đồng ý với chính sách No Refund
     * POST /api/policy/no-refund/acknowledge
     */
    @PostMapping("/no-refund/acknowledge")
    public ResponseEntity<Map<String, Object>> acknowledgeNoRefundPolicy(@RequestBody Map<String, Object> request) {
        Boolean agreed = (Boolean) request.get("agreed");
        
        if (agreed == null || !agreed) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Bạn phải đồng ý với chính sách No Refund để tiếp tục",
                "message", "Vui lòng đọc kỹ và đồng ý với chính sách trước khi đặt vé"
            ));
        }
        
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Bạn đã xác nhận đồng ý với chính sách No Refund của Beta Cinemas",
            "acknowledgedAt", System.currentTimeMillis(),
            "reminder", "Nhớ rằng: Beta Cinemas KHÔNG hoàn/hủy vé sau khi thanh toán"
        ));
    }
}









































