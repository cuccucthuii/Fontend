package org.example.cinema_reservation_system.service.utility;

import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.dto.emaildto.BookingConfirmationEmailDto;
import org.example.cinema_reservation_system.entity.*;
import org.example.cinema_reservation_system.exception.ResourceNotFoundException;
import org.example.cinema_reservation_system.repository.email.EmailHistoryRepository;
import org.example.cinema_reservation_system.repository.email.EmailTemplateRepository;
import org.example.cinema_reservation_system.repository.invoice.InvoiceRepository;
import org.example.cinema_reservation_system.service.utility.BarcodeService;
import org.example.cinema_reservation_system.service.utility.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final EmailTemplateRepository emailTemplateRepository;
    private final EmailHistoryRepository emailHistoryRepository;
    private final InvoiceRepository invoiceRepository;
    private final BarcodeService barcodeService;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.cinema.name:DevCinema}")
    private String cinemaName;

    @Value("${app.cinema.website:http://localhost:5173}")
    private String cinemaWebsite;

    @Override
    public boolean sendBookingConfirmationEmail(Invoice invoice) {
//        try {
//            // Tạo mã giao dịch và mã đặt vé nếu chưa có
//            if (invoice.getMaGiaoDich() == null || invoice.getMaDatVe() == null) {
//                generateTransactionAndBookingCodes(invoice);
//            }
//
//            // Tạo dữ liệu cho email
//            BookingConfirmationEmailDto emailData = createBookingConfirmationData(invoice);
//
//            // Lấy template email
//            EmailTemplate template = getEmailTemplate("BOOKING_CONFIRMATION");
//            if (template == null) {
//                template = createDefaultBookingTemplate();
//            }
//
//            // Tạo nội dung email
//            Context context = new Context();
//            context.setVariable("booking", emailData);
//            context.setVariable("cinemaName", cinemaName);
//            context.setVariable("cinemaWebsite", cinemaWebsite);
//            context.setVariable("currentDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
//
//            String htmlContent = templateEngine.process("booking-confirmation", context);
//            String textContent = template.getNoiDungText();
//
//            // Gửi email
//            boolean sent = sendEmailWithContent(
//                invoice.getKhachHang().getEmail(),
//                template.getTieuDe().replace("{TEN_PHIM}", emailData.getTenPhim()),
//                htmlContent,
//                textContent
//            );
//
//            // Lưu lịch sử
//            saveEmailHistory(invoice, template, htmlContent, textContent, sent);
//
//            // Cập nhật trạng thái email trong hóa đơn
//            if (sent) {
//                invoice.setTrangThaiEmail("DA_GUI");
//                invoice.setNgayGuiEmail(LocalDateTime.now());
//                invoiceRepository.save(invoice);
//            }
//
//            return sent;
//        } catch (Exception e) {
//            // Lưu lịch sử lỗi
//            saveEmailHistory(invoice, null, null, null, false);
//            return false;
//        }
        return false;
    }

    @Override
    public boolean sendEmailWithTemplate(String to, String subject, String templateName, Object data) {
        try {
            EmailTemplate template = emailTemplateRepository.findByTenTemplateAndTrangThaiTrue(templateName)
                    .orElseThrow(() -> new ResourceNotFoundException("Template không tồn tại: " + templateName));

            Context context = new Context();
            context.setVariable("data", data);
            context.setVariable("cinemaName", cinemaName);

            String htmlContent = templateEngine.process(templateName, context);
            String textContent = template.getNoiDungText();

            return sendEmailWithContent(to, subject, htmlContent, textContent);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean sendSimpleEmail(String to, String subject, String content) {
        return sendEmailWithContent(to, subject, content, content);
    }

    @Override
    public void generateTransactionAndBookingCodes(Invoice invoice) {
        // Tạo mã giao dịch (transaction code)
        String transactionCode = generateTransactionCode();
        invoice.setMaGiaoDich(transactionCode);

        // Tạo mã đặt vé (booking code)
        String bookingCode = generateBookingCode();
        invoice.setMaDatVe(bookingCode);

        invoiceRepository.save(invoice);
    }

    @Override
    public String generateBarcode(String bookingCode) {
        // Tạo barcode Base64 để embed vào email
        return barcodeService.generateBarcodeBase64(bookingCode);
    }

    @Override
    public EmailTemplate getEmailTemplate(String templateType) {
        return emailTemplateRepository.findByLoaiTemplateAndTrangThaiTrue(templateType).orElse(null);
    }

    @Override
    public EmailHistory saveEmailHistory(EmailHistory emailHistory) {
        return emailHistoryRepository.save(emailHistory);
    }

    @Override
    public List<EmailHistory> getEmailHistoryByInvoice(Integer invoiceId) {
        return emailHistoryRepository.findByHoaDonIdOrderByNgayGuiDesc(invoiceId);
    }

    @Override
    public boolean retryFailedEmails() {
        List<EmailHistory> failedEmails = emailHistoryRepository.findByTrangThaiAndSoLanThuLaiLessThan("FAILED", 3);

        for (EmailHistory emailHistory : failedEmails) {
            try {
                // Retry gửi email
                boolean sent = sendEmailWithContent(
                        emailHistory.getEmailNguoiNhan(),
                        emailHistory.getTieuDe(),
                        emailHistory.getNoiDungHtml(),
                        emailHistory.getNoiDungText()
                );

                if (sent) {
                    emailHistory.setTrangThai("SENT");
                    emailHistory.setNgayThanhCong(LocalDateTime.now());
                } else {
                    emailHistory.setSoLanThuLai(emailHistory.getSoLanThuLai() + 1);
                }

                emailHistoryRepository.save(emailHistory);
            } catch (Exception e) {
                emailHistory.setSoLanThuLai(emailHistory.getSoLanThuLai() + 1);
                emailHistory.setLoiGui(e.getMessage());
                emailHistoryRepository.save(emailHistory);
            }
        }

        return true;
    }

    @Override
    public Long getEmailStats(String status) {
        return emailHistoryRepository.countByTrangThai(status);
    }

    // Helper methods
    private boolean sendEmailWithContent(String to, String subject, String htmlContent, String textContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML content

            mailSender.send(message);
            return true;
        } catch (MessagingException e) {
            return false;
        }
    }

    private BookingConfirmationEmailDto createBookingConfirmationData(Invoice invoice) {
        BookingConfirmationEmailDto dto = new BookingConfirmationEmailDto();

        // Thông tin giao dịch
        dto.setMaGiaoDich(invoice.getMaGiaoDich());
        dto.setMaDatVe(invoice.getMaDatVe());
        dto.setNgayGiaoDich(invoice.getNgayTao());
        dto.setPhuongThucThanhToan(invoice.getPhuongThucThanhToan());

        // Thông tin khách hàng
        dto.setTenKhachHang(invoice.getTenKhachHang());
        dto.setEmailKhachHang(invoice.getKhachHang().getEmail());
        dto.setSoDienThoai(invoice.getSoDienThoai());

        // Thông tin vé (cần lấy từ CinemaTicket)
        // TODO: Implement logic để lấy thông tin phim, suất chiếu, ghế từ CinemaTicket

        dto.setTongTien(invoice.getTongTien() != null ? java.math.BigDecimal.valueOf(invoice.getTongTien()) : java.math.BigDecimal.ZERO);
        dto.setBarcodeData(generateBarcode(invoice.getMaDatVe()));

        // Thông tin bổ sung
        dto.setChinhSachHoanHuy("Vé đã thanh toán thành công KHÔNG ĐƯỢC HOÀN TIỀN. Chỉ được hoàn tiền khi thanh toán thất bại hoặc lỗi hệ thống.");
        dto.setLienHeHoTro("Hotline: 1900 1234 | Email: support@devcinema.com");

        return dto;
    }

    private String generateTransactionCode() {
        // Tạo mã giao dịch: TXN + timestamp + random
        return "TXN" + System.currentTimeMillis() + new Random().nextInt(1000);
    }

    private String generateBookingCode() {
        // Tạo mã đặt vé: 16 số
        return String.format("%016d", new Random().nextLong()).replace("-", "");
    }

    private EmailTemplate createDefaultBookingTemplate() {
        EmailTemplate template = new EmailTemplate();
        template.setTenTemplate("booking-confirmation");
        template.setTieuDe("Xác nhận đặt vé {TEN_PHIM} thành công - Mã giao dịch {MA_GIAO_DICH}");
        template.setLoaiTemplate("BOOKING_CONFIRMATION");
        template.setTrangThai(true);

        // HTML template sẽ được tạo trong resources/templates
        template.setNoiDungHtml("<!-- HTML template sẽ được load từ file -->");
        template.setNoiDungText("Xác nhận đặt vé thành công");

        return emailTemplateRepository.save(template);
    }

    private void saveEmailHistory(Invoice invoice, EmailTemplate template, String htmlContent, String textContent, boolean success) {
        EmailHistory emailHistory = new EmailHistory();
        emailHistory.setHoaDon(invoice);
        emailHistory.setTemplate(template);
        emailHistory.setEmailNguoiNhan(invoice.getKhachHang().getEmail());
        emailHistory.setTieuDe("Xác nhận đặt vé thành công");
        emailHistory.setNoiDungHtml(htmlContent);
        emailHistory.setNoiDungText(textContent);
        emailHistory.setTrangThai(success ? "SENT" : "FAILED");
        emailHistory.setNgayThanhCong(success ? LocalDateTime.now() : null);

        if (!success) {
            emailHistory.setLoiGui("Lỗi gửi email");
        }

        emailHistoryRepository.save(emailHistory);
    }

    //thêm mới
    public void sendPasswordResetEmail(String to, String username, String tempPassword) {
        String subject = "Mật khẩu tạm cho tài khoản " + username;
        String body = """
        Xin chào %s,

        Hệ thống đã đặt lại mật khẩu cho tài khoản của bạn.
        Mật khẩu tạm: %s

        Vui lòng đăng nhập và đổi mật khẩu ngay trong phần Tài khoản của tôi.

        Trân trọng.
        """.formatted(username, tempPassword);

        var message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    @Override
    public void sendTempPassword(String toEmail, String username, String tempPassword) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(toEmail);
        msg.setSubject("Mật khẩu tạm - DEV CINEMA");
        msg.setText("""
        Xin chào %s,

        Bạn vừa yêu cầu đặt lại mật khẩu. Đây là MẬT KHẨU TẠM của bạn:
        %s

        Vui lòng đăng nhập bằng mật khẩu tạm và ĐỔI MẬT KHẨU ngay trong tài khoản của bạn.

        Nếu bạn không yêu cầu thao tác này, hãy bỏ qua email này.
        """.formatted(username, tempPassword));
        mailSender.send(msg);
    }
}