package org.example.cinema_reservation_system.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import org.example.cinema_reservation_system.utils.enums.TrangThai;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "khach_hang")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_khach_hang")
    private Integer idKhachHang;

    @Column(name = "ten_khach_hang", nullable = false, length = 100)
    private String tenKhachHang;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "so_dien_thoai", nullable = false, length = 15)
    private String soDienThoaiKhachHang;

    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Column(name = "gioi_tinh", length = 10)
    private String gioiTinh;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private TrangThai trangThai = TrangThai.HOAT_DONG;

    @Column(name = "diem_tich_luy")
    private Integer diemTichLuy = 0;

    @Column(name = "cccd", unique = true, length = 12)
    private String cccd;

    @CreationTimestamp
    @Column(name = "ngay_tao", nullable = false, updatable = false)
    private LocalDate ngayTao;

    @Column(name = "hang_thanh_vien", length = 20)
    private String hangThanhVien = "Dong";

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_avatar")
    private Avatar avatar;

    @Column(name = "dia_chi", columnDefinition = "TEXT")
    private String diaChi;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @Column(name = "so_lan_mua_ve")
    private Integer soLanMuaVe = 0;

    @Column(name = "tong_tien_da_mua", precision = 15, scale = 2)
    private BigDecimal tongTienDaMua = BigDecimal.ZERO;

    // Quan hệ với đánh giá phim
    @OneToMany(mappedBy = "khachHang", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovieReview> danhGiaPhim = new ArrayList<>();



    // Thông tin bổ sung
    @Column(name = "so_danh_gia")
    private Integer soDanhGia = 0; // Số lượng đánh giá đã viết
}
