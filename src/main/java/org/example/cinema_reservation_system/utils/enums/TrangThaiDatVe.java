package org.example.cinema_reservation_system.utils.enums;

import lombok.Getter;

/**
 * Enum cho trạng thái đặt vé
 * Di chuyển từ BetaCinemasBooking entity
 */
@Getter
public enum TrangThaiDatVe {
    CHO_THANH_TOAN("CHO_THANH_TOAN", "Chờ thanh toán"),
    DA_THANH_TOAN("DA_THANH_TOAN", "Đã thanh toán"),
    DA_XAC_NHAN("DA_XAC_NHAN", "Đã xác nhận"),
    DA_CHECKIN("DA_CHECKIN", "Đã check-in"),
    DA_HUY("DA_HUY", "Đã hủy"),
    HET_HAN("HET_HAN", "Hết hạn");
    
    private final String code;
    private final String description;
    
    TrangThaiDatVe(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public static TrangThaiDatVe fromCode(String code) {
        for (TrangThaiDatVe trangThai : values()) {
            if (trangThai.code.equals(code)) {
                return trangThai;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy trạng thái đặt vé với code: " + code);
    }
}



































