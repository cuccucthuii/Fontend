package org.example.cinema_reservation_system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "lich_su_khuyen_mai")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lich_su")
    private Integer idLichSu;
    
    @ManyToOne
    @JoinColumn(name = "id_khuyen_mai")
    private Promotion khuyenMai;
    
    @ManyToOne
    @JoinColumn(name = "id_hoa_don")
    private Invoice hoaDon;
    
    @ManyToOne
    @JoinColumn(name = "id_khach_hang")
    private Customer khachHang;
    
    @Column(name = "so_tien_giam", precision = 15, scale = 2)
    private BigDecimal soTienGiam;
    
    @CreationTimestamp
    @Column(name = "ngay_su_dung", nullable = false, updatable = false)
    private LocalDateTime ngaySuDung;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", length = 20)
    private TrangThaiSuDung trangThai = TrangThaiSuDung.THANH_CONG;
    
    public enum TrangThaiSuDung {
        THANH_CONG,     // Sử dụng thành công
        THAT_BAI,       // Sử dụng thất bại
        HOAN_TRA        // Hoàn trả khuyến mãi
    }
}















































