package org.example.cinema_reservation_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "mon_an_uong")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mon")
    private Integer idMon;
    
    @Column(name = "ten_mon", nullable = false, length = 100)
    private String tenMon;
    
    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;
    
    @Column(name = "gia_mon", nullable = false, precision = 10, scale = 2)
    private BigDecimal giaMon;
    
    @Column(name = "loai_mon", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private LoaiMon loaiMon;
    
    @Column(name = "hinh_anh", length = 255)
    private String hinhAnh;
    
    @Column(name = "trang_thai", length = 20)
    @Enumerated(EnumType.STRING)
    private TrangThaiMon trangThai = TrangThaiMon.HOAT_DONG;
    
    @CreationTimestamp
    @Column(name = "ngay_tao", nullable = false, updatable = false)
    private LocalDateTime ngayTao;
    
    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;
    
    public enum LoaiMon {
        BOP_RANG_BO,      // Bỏng rang bơ
        NUOC_NGOT,        // Nước ngọt
        NUOC_EP,          // Nước ép
        SNACK,            // Snack khoai tây
        KEM,              // Kem
        KEO,              // Kẹo
        BANH,             // Bánh
        HAT,              // Hạt hướng dương
        KHAC              // Khác
    }
    
    public enum TrangThaiMon {
        HOAT_DONG,
        KHONG_HOAT_DONG,
        HET_HANG
    }
}
