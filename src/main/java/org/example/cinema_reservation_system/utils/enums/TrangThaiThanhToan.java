package org.example.cinema_reservation_system.utils.enums;

public enum TrangThaiThanhToan {
    CHO_THANH_TOAN("Chờ thanh toán"),
    DA_THANH_TOAN("Đã thanh toán"),
    THANH_TOAN_THAT_BAI("Thanh toán thất bại"),
    HOAN_TIEN("Hoàn tiền"),
    HUY("Hủy");
    
    private final String description;
    
    TrangThaiThanhToan(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}


































