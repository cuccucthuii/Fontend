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
@Table(name = "combo_an_uong")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodCombo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_combo")
    private Integer idCombo;
    
    @Column(name = "ten_combo", nullable = false, length = 100)
    private String tenCombo;
    
    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;
    
    @Column(name = "gia_combo", nullable = false, precision = 10, scale = 2)
    private BigDecimal giaCombo;
    
    @Column(name = "trang_thai", length = 20)
    @Enumerated(EnumType.STRING)
    private TrangThaiCombo trangThai = TrangThaiCombo.HOAT_DONG;
    
    @CreationTimestamp
    @Column(name = "ngay_tao", nullable = false, updatable = false)
    private LocalDateTime ngayTao;
    
    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;
    
    public enum TrangThaiCombo {
        HOAT_DONG,
        KHONG_HOAT_DONG
    }
}


