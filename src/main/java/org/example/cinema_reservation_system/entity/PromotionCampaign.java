package org.example.cinema_reservation_system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chien_dich_khuyen_mai")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionCampaign {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_chien_dich")
    private Integer idChienDich;
    
    @Column(name = "ten_chien_dich", nullable = false, length = 200)
    private String tenChienDich;
    
    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "loai_chien_dich", nullable = false, length = 50)
    private LoaiChienDich loaiChienDich;
    
    @Column(name = "ngay_bat_dau", nullable = false)
    private LocalDateTime ngayBatDau;
    
    @Column(name = "ngay_ket_thuc", nullable = false)
    private LocalDateTime ngayKetThuc;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", length = 20)
    private TrangThaiChienDich trangThai = TrangThaiChienDich.HOAT_DONG;
    
    @Column(name = "hinh_anh_banner", length = 500)
    private String hinhAnhBanner;
    
    @Column(name = "mau_sac", length = 20)
    private String mauSac;
    
    @Column(name = "uu_tien")
    private Integer uuTien = 0;
    
    @CreationTimestamp
    @Column(name = "ngay_tao", nullable = false, updatable = false)
    private LocalDateTime ngayTao;
    
    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;
    
    public enum LoaiChienDich {
        SU_KIEN,        // Sự kiện (Tết, Valentine, Halloween)
        THOI_GIAN,      // Thời gian (Flash Sale, Happy Hour)
        DOI_TUONG,      // Đối tượng (Sinh viên, Trẻ em, VIP)
        FLASH_SALE      // Flash Sale
    }
    
    public enum TrangThaiChienDich {
        HOAT_DONG,      // Đang hoạt động
        TAM_DUNG,       // Tạm dừng
        KET_THUC        // Kết thúc
    }
}







