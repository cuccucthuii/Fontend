package org.example.cinema_reservation_system.utils.enums;

public enum TrangThaiUserAccount {
    HOAT_DONG("Hoạt động"),
    KHOA("Khóa"),
    CHO_XAC_THUC("Chờ xác thực"),
    TAM_KHOA("Tạm khóa");
    
    private final String description;
    
    TrangThaiUserAccount(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}


































