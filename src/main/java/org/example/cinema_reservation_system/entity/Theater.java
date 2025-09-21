package org.example.cinema_reservation_system.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import org.example.cinema_reservation_system.utils.enums.TrangThai;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "rapChieu")
public class Theater {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rap_chieu")
    private Integer idRapChieu;

    @Column(name = "ten_rap_chieu", nullable = false, unique = true, length = 100)
    private String tenRapChieu;

    @Column(name = "dia_chi", nullable = false, length = 255)
    private String diaChi;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    private TrangThai trangThai = TrangThai.HOAT_DONG;

    @Column(name = "so_dien_thoai", nullable = false, length = 10)
    private String soDienThoai;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    @Column(name = "thanh_pho", length = 100)
    private String thanhPho;

    @Column(name = "khu_vuc", length = 100)
    private String khuVuc;

    @CreationTimestamp
    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @Column(name = "so_phong_chieu")
    private Integer soPhongChieu = 0;

    @Column(name = "tong_so_ghe")
    private Integer tongSoGhe = 0;

    // Tọa độ địa lý
    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;
}
