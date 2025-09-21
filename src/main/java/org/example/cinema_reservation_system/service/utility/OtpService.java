package org.example.cinema_reservation_system.service.utility;

public interface OtpService {
    String generateAndSendOTP(String phone, String email);
    boolean verifyOTP(String phone, String otp);
    int getOTPExpiration();
}