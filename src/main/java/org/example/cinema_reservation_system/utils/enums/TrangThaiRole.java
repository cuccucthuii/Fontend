package org.example.cinema_reservation_system.utils.enums;

public enum TrangThaiRole {
    HOAT_DONG("Hoạt động"),
    KHOA("Khóa"),
    TAM_KHOA("Tạm khóa");
    
    private final String description;
    
    TrangThaiRole(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}


































