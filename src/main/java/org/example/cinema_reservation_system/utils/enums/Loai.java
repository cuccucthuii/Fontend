package org.example.cinema_reservation_system.utils.enums;

import lombok.Getter;

/**
 * Enum chung cho các loại trong hệ thống
 */
@Getter
public enum Loai {
    // Loại vé
    VE_THUONG("VE_THUONG", "Vé thường"),
    VE_3D("VE_3D", "Vé 3D"),
    VE_IMAX("VE_IMAX", "Vé IMAX"),
    VE_4DX("VE_4DX", "Vé 4DX"),
    VE_VIP("VE_VIP", "Vé VIP"),
    
    // Loại giảm giá
    PHAN_TRAM("PHAN_TRAM", "Giảm theo phần trăm"),
    TIEN_MAT("TIEN_MAT", "Giảm tiền mặt"),
    
    // Loại chiến dịch
    SU_KIEN("SU_KIEN", "Sự kiện"),
    THOI_GIAN("THOI_GIAN", "Thời gian"),
    DOI_TUONG("DOI_TUONG", "Đối tượng"),
    FLASH_SALE("FLASH_SALE", "Flash Sale"),
    
    // Loại hình ảnh
    POSTER("POSTER", "Poster"),
    BANNER("BANNER", "Banner"),
    
    // Loại media
    IMAGE("IMAGE", "Hình ảnh"),
    VIDEO("VIDEO", "Video"),
    
    // Loại món ăn
    BOP_RANG_BO("BOP_RANG_BO", "Bỏng rang bơ"),
    NUOC_NGOT("NUOC_NGOT", "Nước ngọt"),
    NUOC_EP("NUOC_EP", "Nước ép"),
    BANH_NGOT("BANH_NGOT", "Bánh ngọt"),
    KEM("KEM", "Kem"),
    HOT_DOG("HOT_DOG", "Hot dog"),
    COMBO("COMBO", "Combo");
    
    private final String code;
    private final String description;
    
    Loai(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public static Loai fromCode(String code) {
        for (Loai loai : values()) {
            if (loai.code.equals(code)) {
                return loai;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy loại với code: " + code);
    }
}
