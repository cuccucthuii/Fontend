package org.example.cinema_reservation_system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "khuyen_mai")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_khuyen_mai")
    private Integer idKhuyenMai;
    
    @ManyToOne
    @JoinColumn(name = "id_chien_dich", nullable = false)
    private PromotionCampaign chienDich;
    
    @Column(name = "ten_khuyen_mai", nullable = false, length = 200)
    private String tenKhuyenMai;
    
    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "loai_giam", nullable = false, length = 20)
    private LoaiGiam loaiGiam;
    
    @Column(name = "gia_tri_giam", precision = 15, scale = 2)
    private BigDecimal giaTriGiam;
    
    @Column(name = "phan_tram_giam", precision = 5, scale = 2)
    private BigDecimal phanTramGiam;
    
    @Column(name = "muc_giam_toi_da", precision = 15, scale = 2)
    private BigDecimal mucGiamToiDa;
    
    @Column(name = "dieu_kien_ap_dung", columnDefinition = "TEXT")
    private String dieuKienApDung;
    
    @Column(name = "so_luong_toi_da")
    private Integer soLuongToiDa = -1; // -1 = không giới hạn
    
    @Column(name = "so_luong_da_su_dung")
    private Integer soLuongDaSuDung = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", length = 20)
    private TrangThaiKhuyenMai trangThai = TrangThaiKhuyenMai.HOAT_DONG;
    
    @CreationTimestamp
    @Column(name = "ngay_tao", nullable = false, updatable = false)
    private LocalDateTime ngayTao;
    
    
    public enum LoaiGiam {
        PHAN_TRAM,      // Giảm theo phần trăm
        TIEN_MAT        // Giảm tiền mặt
    }
    
    public enum TrangThaiKhuyenMai {
        HOAT_DONG,      // Đang hoạt động
        HET_HAN,        // Hết hạn
        TAM_DUNG        // Tạm dừng
    }
    
    // Phương thức kiểm tra còn khuyến mãi không
    public boolean conKhuyenMai() {
        return trangThai == TrangThaiKhuyenMai.HOAT_DONG 
            && (soLuongToiDa == -1 || soLuongDaSuDung < soLuongToiDa);
    }
    
    // Phương thức tính tiền giảm
    public BigDecimal tinhTienGiam(BigDecimal tongTien) {
        if (loaiGiam == LoaiGiam.TIEN_MAT) {
            return giaTriGiam != null ? giaTriGiam : BigDecimal.ZERO;
        } else if (loaiGiam == LoaiGiam.PHAN_TRAM) {
            BigDecimal tienGiam = tongTien.multiply(phanTramGiam).divide(BigDecimal.valueOf(100));
            if (mucGiamToiDa != null && tienGiam.compareTo(mucGiamToiDa) > 0) {
                return mucGiamToiDa;
            }
            return tienGiam;
        }
        return BigDecimal.ZERO;
    }
}


