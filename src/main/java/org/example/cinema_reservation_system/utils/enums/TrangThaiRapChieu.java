package org.example.cinema_reservation_system.utils.enums;

public enum TrangThaiRapChieu {
    HOAT_DONG("Hoạt động"),
    BAO_TRI("Bảo trì"),
    KHOA("Khóa"),
    TAM_KHOA("Tạm khóa");
    
    private final String description;
    
    TrangThaiRapChieu(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}


































