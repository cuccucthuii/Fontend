package org.example.cinema_reservation_system.service.utility;

import org.example.cinema_reservation_system.dto.emaildto.BookingConfirmationEmailDto;
import org.example.cinema_reservation_system.entity.EmailHistory;
import org.example.cinema_reservation_system.entity.EmailTemplate;
import org.example.cinema_reservation_system.entity.Invoice;

import java.util.List;

public interface EmailService {

    // Gửi email xác nhận đặt vé
    boolean sendBookingConfirmationEmail(Invoice invoice);

    // Gửi email với template tùy chỉnh
    boolean sendEmailWithTemplate(String to, String subject, String templateName, Object data);

    // Gửi email đơn giản
    boolean sendSimpleEmail(String to, String subject, String content);

    // Tạo mã giao dịch và mã đặt vé
    void generateTransactionAndBookingCodes(Invoice invoice);

    // Tạo barcode cho vé
    String generateBarcode(String bookingCode);

    // Lấy template email
    EmailTemplate getEmailTemplate(String templateType);

    // Lưu lịch sử gửi email
    EmailHistory saveEmailHistory(EmailHistory emailHistory);

    // Lấy lịch sử email theo hóa đơn
    List<EmailHistory> getEmailHistoryByInvoice(Integer invoiceId);

    // Retry gửi email thất bại
    boolean retryFailedEmails();

    // Thống kê email
    Long getEmailStats(String status);

    //thêm mới
    //Email reset mật khẩu
    void sendPasswordResetEmail(String email, String username, String plain);

    void sendTempPassword(String toEmail, String username, String tempPassword);
}

