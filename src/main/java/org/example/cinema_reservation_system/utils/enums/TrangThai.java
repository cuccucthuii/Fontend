package org.example.cinema_reservation_system.utils.enums;

import lombok.Getter;

/**
 * Enum chung cho tất cả các trạng thái trong hệ thống
 * Thay thế cho nhiều enum TrangThai* riêng lẻ
 */
@Getter
public enum TrangThai {
    // Trạng thái chung
    HOAT_DONG("HOAT_DONG", "Hoạt động"),
    KHONG_HOAT_DONG("KHONG_HOAT_DONG", "Không hoạt động"),
    BI_CAM("BI_CAM", "Bị cấm"),
    BAO_TRI("BAO_TRI", "Bảo trì"),
    TAM_DUNG("TAM_DUNG", "Tạm dừng"),
    HET_HAN("HET_HAN", "Hết hạn"),
    KET_THUC("KET_THUC", "Kết thúc"),
    
    // Trạng thái phim
    DANG_CHIEU("DANG_CHIEU", "Đang chiếu"),
    SAP_CHIEU("SAP_CHIEU", "Sắp chiếu"),
    NGUNG_CHIEU("NGUNG_CHIEU", "Ngừng chiếu"),
    
    // Trạng thái ghế
    CON_TRONG("CON_TRONG", "Còn trống"),
    DA_DAT("DA_DAT", "Đã đặt"),
    DANG_SU_DUNG("DANG_SU_DUNG", "Đang sử dụng"),
    
    // Trạng thái thanh toán
    CHO_THANH_TOAN("CHO_THANH_TOAN", "Chờ thanh toán"),
    CHO_XU_LY("CHO_XU_LY", "Chờ xử lý"),
    DA_THANH_TOAN("DA_THANH_TOAN", "Đã thanh toán"),
    DA_HUY("DA_HUY", "Đã hủy"),
    
    // Trạng thái đặt vé
    DA_XAC_NHAN("DA_XAC_NHAN", "Đã xác nhận"),
    DA_CHECKIN("DA_CHECKIN", "Đã check-in"),
    DA_LEN_LICH("DA_LEN_LICH", "Đã lên lịch"),
    HOAN_THANH("HOAN_THANH", "Hoàn thành"),
    
    // Trạng thái khuyến mãi
    HET_HANG("HET_HANG", "Hết hàng"),
    
    // Trạng thái check-in
    DA_CHECKIN_VE("DA_CHECKIN", "Đã check-in vé"),
    
    // Trạng thái sử dụng
    THANH_CONG("THANH_CONG", "Thành công"),
    THAT_BAI("THAT_BAI", "Thất bại"),
    HOAN_TRA("HOAN_TRA", "Hoàn trả"),
    
    // Trạng thái voucher
    DA_SU_DUNG("DA_SU_DUNG", "Đã sử dụng"),
    CHUA_SU_DUNG("CHUA_SU_DUNG", "Chưa sử dụng");
    
    private final String code;
    private final String description;
    
    TrangThai(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    /**
     * Tìm enum theo code
     */
    public static TrangThai fromCode(String code) {
        for (TrangThai trangThai : values()) {
            if (trangThai.code.equals(code)) {
                return trangThai;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy trạng thái với code: " + code);
    }
    
    /**
     * Kiểm tra xem có phải trạng thái hoạt động không
     */
    public boolean isActive() {
        return this == HOAT_DONG || this == DANG_CHIEU || this == CON_TRONG;
    }
    
    /**
     * Kiểm tra xem có phải trạng thái đã hoàn thành không
     */
    public boolean isCompleted() {
        return this == DA_THANH_TOAN || this == DA_CHECKIN || this == THANH_CONG;
    }
}




