package org.example.cinema_reservation_system.utils.enums;

import lombok.Getter;

/**
 * Enum chung cho các phương thức trong hệ thống
 */
@Getter
public enum PhuongThuc {
    // Phương thức thanh toán
    TIEN_MAT("TIEN_MAT", "Tiền mặt"),
    CHUYEN_KHOAN("CHUYEN_KHOAN", "Chuyển khoản"),
    VNPAY("VNPAY", "VNPay"),
    
    // Phương thức gửi tin nhắn
    SMS("SMS", "SMS"),
    EMAIL("EMAIL", "Email"),
    PUSH_NOTIFICATION("PUSH_NOTIFICATION", "Push notification");
    
    private final String code;
    private final String description;
    
    PhuongThuc(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public static PhuongThuc fromCode(String code) {
        for (PhuongThuc phuongThuc : values()) {
            if (phuongThuc.code.equals(code)) {
                return phuongThuc;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy phương thức với code: " + code);
    }
}

