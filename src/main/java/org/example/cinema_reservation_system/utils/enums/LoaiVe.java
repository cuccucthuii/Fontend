package org.example.cinema_reservation_system.utils.enums;

import lombok.Getter;

/**
 * Enum cho loại vé - Sử dụng Loai enum chung
 * @deprecated Sử dụng Loai enum thay thế
 */
@Getter
@Deprecated
public enum LoaiVe {
    VE_THUONG("VE_THUONG", "Vé thường"),
    VE_3D("VE_3D", "Vé 3D"),
    VE_IMAX("VE_IMAX", "Vé IMAX"),
    VE_4DX("VE_4DX", "Vé 4DX"),
    VE_VIP("VE_VIP", "Vé VIP");

    private final String code;
    private final String description;

    LoaiVe(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static LoaiVe fromCode(String code) {
        for (LoaiVe loaiVe : values()) {
            if (loaiVe.code.equals(code)) {
                return loaiVe;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy loại vé với code: " + code);
    }

    /**
     * Chuyển đổi sang Loai enum
     */
    public Loai toLoai() {
        return Loai.fromCode(this.code);
    }

    /**
     * Tạo LoaiVe từ Loai enum
     */
    public static LoaiVe fromLoai(Loai loai) {
        if (loai == null) return null;

        switch (loai) {
            case VE_THUONG: return VE_THUONG;
            case VE_3D: return VE_3D;
            case VE_IMAX: return VE_IMAX;
            case VE_4DX: return VE_4DX;
            case VE_VIP: return VE_VIP;
            default: throw new IllegalArgumentException("Loai không phải là loại vé: " + loai);
        }
    }
}

