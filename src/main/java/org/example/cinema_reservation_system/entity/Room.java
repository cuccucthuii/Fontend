package org.example.cinema_reservation_system.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import org.example.cinema_reservation_system.utils.enums.TrangThai;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "phong_chieu")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_phong_chieu")
    private Integer idPhongChieu;

    @Column(name = "ten_phong_chieu", nullable = false, length = 100)
    private String tenPhongChieu;

    @Column(name = "dien_tich_phong", nullable = false)
    private BigDecimal dienTichPhong;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    private TrangThai trangThai = TrangThai.HOAT_DONG;

    @ManyToOne
    @JoinColumn(name = "id_rap_chieu", nullable = false)
    private Theater rapChieu;

    // ===== SOFT DELETE FIELDS =====
    @Column(name = "da_xoa", nullable = false)
    private Boolean daXoa;

    @Column(name = "ngay_xoa")
    private LocalDateTime ngayXoa;

    @Column(name = "nguoi_xoa")
    private String nguoiXoa;

}
