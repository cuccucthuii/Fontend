package org.example.cinema_reservation_system.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.example.cinema_reservation_system.service.EmailService;
import org.example.cinema_reservation_system.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
public class OtpServiceImpl implements OtpService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${otp.expiration:120}")  // Default 120 seconds
    private int otpExpiration;

    @Value("${otp.length:6}")       // Default 6 digits
    private int otpLength;

    @Value("${otp.max-attempts:3}") // Default 3 attempts
    private int maxAttempts;

    // In-memory storage for OTP (in production, use Redis or database)
    private final Map<String, OTPData> otpStorage = new HashMap<>();

    public String generateAndSendOTP(String phone, String email) {
        // Generate OTP
        String otp = generateOTP();

        // Store OTP with expiration
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(otpExpiration);
        OTPData otpData = new OTPData(otp, expiresAt, 0);
        otpStorage.put(phone, otpData);

        // Log OTP for development (remove in production)
        log.info("Generated OTP for phone {}: {}", phone, otp);

        // Send OTP via email
        sendOTPEmail(email, otp);

        return otp;
    }

    public boolean verifyOTP(String phone, String otp) {
        OTPData otpData = otpStorage.get(phone);

        if (otpData == null) {
            log.warn("No OTP found for phone: {}", phone);
            return false;
        }

        // Check if OTP is expired
        if (LocalDateTime.now().isAfter(otpData.getExpiresAt())) {
            log.warn("OTP expired for phone: {}", phone);
            otpStorage.remove(phone);
            return false;
        }

        // Check if max attempts exceeded
        if (otpData.getAttempts() >= maxAttempts) {
            log.warn("Max attempts exceeded for phone: {}", phone);
            otpStorage.remove(phone);
            return false;
        }

        // Increment attempts
        otpData.incrementAttempts();

        // Verify OTP
        if (otpData.getOtp().equals(otp)) {
            log.info("OTP verified successfully for phone: {}", phone);
            otpStorage.remove(phone); // Remove after successful verification
            return true;
        }

        log.warn("Invalid OTP for phone: {}", phone);
        return false;
    }

    private String generateOTP() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < otpLength; i++) {
            otp.append(random.nextInt(10));
        }

        return otp.toString();
    }

    private void sendOTPEmail(String email, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Mã OTP xác thực - DEV CINEMA");
            message.setText(String.format(
                    "Chào bạn,\n\n" +
                            "Mã OTP xác thực của bạn là: %s\n\n" +
                            "Mã này sẽ hết hạn sau %d giây.\n" +
                            "Vui lòng không chia sẻ mã này với bất kỳ ai.\n\n" +
                            "Trân trọng,\n" +
                            "DEV CINEMA Team",
                    otp, otpExpiration
            ));

            mailSender.send(message);
            log.info("OTP email sent successfully to: {}", email);

        } catch (Exception e) {
            log.error("Failed to send OTP email to: {}", email, e);
            throw new RuntimeException("Không thể gửi email OTP", e);
        }
    }

    public int getOTPExpiration() {
        return otpExpiration;
    }

    // Inner class to store OTP data
    private static class OTPData {
        private final String otp;
        private final LocalDateTime expiresAt;
        private int attempts;

        public OTPData(String otp, LocalDateTime expiresAt, int attempts) {
            this.otp = otp;
            this.expiresAt = expiresAt;
            this.attempts = attempts;
        }

        public String getOtp() { return otp; }
        public LocalDateTime getExpiresAt() { return expiresAt; }
        public int getAttempts() { return attempts; }
        public void incrementAttempts() { this.attempts++; }
    }
}
