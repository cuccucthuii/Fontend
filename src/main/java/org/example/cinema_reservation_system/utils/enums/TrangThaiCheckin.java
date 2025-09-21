package org.example.cinema_reservation_system.utils.enums;

import lombok.Getter;

/**
 * Enum cho trạng thái check-in
 * Di chuyển từ TicketCheckin entity
 */
@Getter
public enum TrangThaiCheckin {
    DA_CHECKIN("DA_CHECKIN", "Đã check-in"),
    DA_HUY("DA_HUY", "Đã hủy");
    
    private final String code;
    private final String description;
    
    TrangThaiCheckin(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public static TrangThaiCheckin fromCode(String code) {
        for (TrangThaiCheckin trangThai : values()) {
            if (trangThai.code.equals(code)) {
                return trangThai;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy trạng thái check-in với code: " + code);
    }
}



































