package org.example.cinema_reservation_system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import org.example.cinema_reservation_system.utils.enums.TrangThai;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "danh_gia_phim")
public class MovieReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_danh_gia")
    private Integer idDanhGia;

    @ManyToOne
    @JoinColumn(name = "id_phim", nullable = false)
    private Movie phim;

    @ManyToOne
    @JoinColumn(name = "id_khach_hang", nullable = false)
    private Customer khachHang;

    @Column(name = "so_sao", nullable = false)
    private Integer diemDanhGia; // 1-5 sao

    @Column(name = "noi_dung_danh_gia")
    private String noiDungDanhGia;

    @Column(name = "ngay_danh_gia")
    private LocalDateTime ngayDanhGia;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private TrangThai trangThai = TrangThai.HOAT_DONG;

    // Tiêu đề đánh giá (tùy chọn)
    @Column(name = "tieu_de_danh_gia")
    private String tieuDeDanhGia;

    @Column(name = "co_anh_kem_theo")
    private Boolean coAnhKemTheo = false;

    @Column(name = "so_luot_thich")
    private Integer soLuotThich = 0;

    @Column(name = "so_luot_phan_hoi")
    private Integer soLuotPhanHoi = 0;

    @Column(name = "da_xem_phim")
    private Boolean daXemPhim = false;

    @Column(name = "ngay_xem_phim")
    private LocalDateTime ngayXemPhim;

    @Column(name = "rap_chieu")
    private String rapChieu;

    @Column(name = "loai_ve")
    private String loaiVe; // 2D, 3D, IMAX, VIP

    @Column(name = "gia_ve", precision = 10, scale = 2)
    private BigDecimal giaVe;

    // Quan hệ với media đánh giá (ảnh + video)
    @OneToMany(mappedBy = "movieReview", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewMedia> reviewMedia = new ArrayList<>();

    // Quan hệ với tags đánh giá
    @OneToMany(mappedBy = "movieReview", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewTag> reviewTags = new ArrayList<>();

    // Quan hệ với phản hồi
    @OneToMany(mappedBy = "movieReview", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewReply> phanHoi = new ArrayList<>();

    // Thông tin bổ sung
    @Column(name = "phuong_thuc_mua")
    private String phuongThucMua; // MoMo, VNPay, Tiền mặt, etc.

    @Column(name = "diem_cong")
    private Integer diemCong = 0; // Điểm cộng cho đánh giá

    @Column(name = "co_video")
    private Boolean coVideo = false;

    @Column(name = "so_luot_xem")
    private Integer soLuotXem = 0;

    @PrePersist
    protected void onCreate() {
        this.ngayDanhGia = LocalDateTime.now();
        if (this.trangThai == null) {
            this.trangThai = TrangThai.HOAT_DONG;
        }
    }

    // Điểm trung bình (đơn giản: chính là số sao tổng)
    public Double getDiemTrungBinh() {
        return diemDanhGia != null ? diemDanhGia.doubleValue() : 0.0;
    }

    // Kiểm tra đánh giá có hợp lệ
    public boolean isValidReview() {
        return diemDanhGia != null && diemDanhGia >= 1 && diemDanhGia <= 5 &&
               noiDungDanhGia != null && !noiDungDanhGia.trim().isEmpty();
    }
}

