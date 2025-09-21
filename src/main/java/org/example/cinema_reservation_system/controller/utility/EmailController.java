package org.example.cinema_reservation_system.controller.utility;

import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.entity.EmailHistory;
import org.example.cinema_reservation_system.entity.EmailTemplate;
import org.example.cinema_reservation_system.entity.Invoice;
import org.example.cinema_reservation_system.service.utility.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class EmailController {

    private final EmailService emailService;

    // Gửi email xác nhận đặt vé
    @PostMapping("/booking-confirmation/{invoiceId}")
    public ResponseEntity<String> sendBookingConfirmationEmail(@PathVariable Integer invoiceId) {
        // TODO: Lấy invoice từ database
        // Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        // boolean sent = emailService.sendBookingConfirmationEmail(invoice);
        
        boolean sent = true; // Mock for now
        if (sent) {
            return ResponseEntity.ok("Email xác nhận đã được gửi thành công!");
        } else {
            return ResponseEntity.badRequest().body("Lỗi gửi email xác nhận");
        }
    }

    // Retry gửi email thất bại
    @PostMapping("/retry-failed")
    public ResponseEntity<String> retryFailedEmails() {
        boolean success = emailService.retryFailedEmails();
        if (success) {
            return ResponseEntity.ok("Đã thử gửi lại các email thất bại");
        } else {
            return ResponseEntity.badRequest().body("Lỗi khi thử gửi lại email");
        }
    }

    // Lấy lịch sử email theo hóa đơn
    @GetMapping("/history/{invoiceId}")
    public ResponseEntity<List<EmailHistory>> getEmailHistory(@PathVariable Integer invoiceId) {
        List<EmailHistory> history = emailService.getEmailHistoryByInvoice(invoiceId);
        return ResponseEntity.ok(history);
    }

    // Thống kê email
    @GetMapping("/stats/{status}")
    public ResponseEntity<Long> getEmailStats(@PathVariable String status) {
        Long count = emailService.getEmailStats(status);
        return ResponseEntity.ok(count);
    }

    // Tạo mã giao dịch và mã đặt vé
    @PostMapping("/generate-codes/{invoiceId}")
    public ResponseEntity<String> generateTransactionAndBookingCodes(@PathVariable Integer invoiceId) {
        // TODO: Lấy invoice từ database
        // Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        // emailService.generateTransactionAndBookingCodes(invoice);
        
        return ResponseEntity.ok("Đã tạo mã giao dịch và mã đặt vé");
    }

    // Test gửi email đơn giản
    @PostMapping("/test")
    public ResponseEntity<String> testEmail(@RequestParam String to, @RequestParam String subject, @RequestParam String content) {
        boolean sent = emailService.sendSimpleEmail(to, subject, content);
        if (sent) {
            return ResponseEntity.ok("Email test đã được gửi thành công!");
        } else {
            return ResponseEntity.badRequest().body("Lỗi gửi email test");
        }
    }
}










