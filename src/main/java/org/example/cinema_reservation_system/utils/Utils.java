package org.example.cinema_reservation_system.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

/**
 * Utility class chứa các method tiện ích chung cho hệ thống
 */
public class Utils {
    
    private static final Random random = new Random();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * Tạo mã ngẫu nhiên với độ dài chỉ định
     */
    public static String generateRandomCode(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        
        for (int i = 0; i < length; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }
        
        return code.toString();
    }
    
    /**
     * Tạo mã đặt vé
     */
    public static String generateBookingCode() {
        return "BETA_" + generateRandomCode(8);
    }
    
    /**
     * Tạo mã giao dịch
     */
    public static String generateTransactionCode() {
        return "TXN_" + System.currentTimeMillis() + "_" + generateRandomCode(4);
    }
    
    /**
     * Tạo UUID ngắn
     */
    public static String generateShortUUID() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
    
    /**
     * Format LocalDateTime thành String
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_TIME_FORMATTER) : null;
    }
    
    /**
     * Format LocalDateTime thành String chỉ ngày
     */
    public static String formatDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_FORMATTER) : null;
    }
    
    /**
     * Kiểm tra email hợp lệ
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
    
    /**
     * Kiểm tra số điện thoại Việt Nam hợp lệ
     */
    public static boolean isValidVietnamesePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        // Loại bỏ khoảng trắng và ký tự đặc biệt
        String cleanPhone = phone.replaceAll("[\\s\\-()]", "");
        // Kiểm tra format số điện thoại Việt Nam
        return cleanPhone.matches("^(\\+84|84|0)[1-9][0-9]{8,9}$");
    }
    
    /**
     * Làm sạch số điện thoại (chỉ giữ lại số)
     */
    public static String cleanPhoneNumber(String phone) {
        if (phone == null) return null;
        return phone.replaceAll("[^0-9]", "");
    }
    
    /**
     * Tạo số ngẫu nhiên trong khoảng
     */
    public static int randomInt(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }
    
    /**
     * Kiểm tra chuỗi null hoặc rỗng
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Làm sạch chuỗi (trim và loại bỏ khoảng trắng thừa)
     */
    public static String cleanString(String str) {
        return str != null ? str.trim().replaceAll("\\s+", " ") : null;
    }
    
    /**
     * Tạo mã QR code cho vé
     */
    public static String generateQRCode(String bookingCode, String seatInfo) {
        return String.format("BETA_CINEMA|%s|%s|%s", 
            bookingCode, seatInfo, System.currentTimeMillis());
    }
    
    /**
     * Tính phần trăm giảm giá
     */
    public static double calculateDiscountPercentage(double originalPrice, double discountedPrice) {
        if (originalPrice <= 0) return 0;
        return ((originalPrice - discountedPrice) / originalPrice) * 100;
    }
    
    /**
     * Làm tròn số tiền
     */
    public static double roundMoney(double amount) {
        return Math.round(amount * 100.0) / 100.0;
    }
}
