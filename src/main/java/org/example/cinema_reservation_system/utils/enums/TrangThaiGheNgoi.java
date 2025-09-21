package org.example.cinema_reservation_system.utils.enums;

public enum TrangThaiGheNgoi {
    TRONG("Trống"),
    DA_DAT("Đã đặt"),
    DA_BAN("Đã bán"),
    HUY("Hủy"),
    BAO_TRI("Bảo trì");
    
    private final String description;
    
    TrangThaiGheNgoi(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}


































