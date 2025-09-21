package org.example.cinema_reservation_system.entity;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import lombok.ToString;
import org.example.cinema_reservation_system.utils.enums.TrangThai;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "phim")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_phim")
    private Integer idPhim;

    @Column(name = "ten_phim", nullable = false, length = 100)
    private String tenPhim;

    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "thoi_luong", nullable = false)
    private Integer thoiLuong;

    @Column(name = "ngay_phat_hanh", nullable = false)
    private LocalDate ngayPhatHanh;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    private TrangThai trangThai = TrangThai.NGUNG_CHIEU;

    @Column(name = "dinh_dang", nullable = false, length = 50)
    @Pattern(regexp = "2D|3D|4D|IMAX", message = "Định dạng phải là 2D, 3D, 4D hoặc IMAX")
    private String dinhDang;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDate ngayTao;

    @Column(name = "tuoi_gioi_han", nullable = false, length = 10)
    private String tuoiGioiHan = "P"; // "P", "T13", "T16", "T18"

    @Column(name = "nam_san_xuat", nullable = false)
    private Integer namSanXuat;

    @Column(name = "do_pho_bien", length = 20)
    private String doPhoBien = "PHO_BIEN"; // "PHO_BIEN", "IT_PHO_BIEN", "DAC_BIET"

    @Column(name = "gia_ve_co_ban", nullable = false, precision = 10, scale = 2)
    private BigDecimal giaVeCoBan = new BigDecimal("50000.00");

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @Column(name = "luot_xem")
    private Integer luotXem = 0;

    @Column(name = "danh_gia_trung_binh", precision = 3, scale = 2)
    private BigDecimal danhGiaTrungBinh = BigDecimal.ZERO;

    @PrePersist
    protected void onCreate() {
        this.ngayTao = LocalDate.now();
        if (this.namSanXuat == null) {
            this.namSanXuat = LocalDate.now().getYear();
        }
    }

    @ToString.Exclude
    @OneToMany(mappedBy = "phim", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Image> hinhAnhs = new HashSet<>();

    @OneToOne(mappedBy = "phim", cascade = CascadeType.ALL, orphanRemoval = true)
    private Trailer trailer;

    // ====================== QUAN HỆ ==========================

    @ManyToMany
    @JoinTable(
            name = "phim_the_loai",
            joinColumns = @JoinColumn(name = "id_phim"),
            inverseJoinColumns = @JoinColumn(name = "id_the_loai_phim")
    )
    private Set<Genre> theLoaiList;

    @ManyToMany
    @JoinTable(
            name = "phim_dao_dien",
            joinColumns = @JoinColumn(name = "id_phim"),
            inverseJoinColumns = @JoinColumn(name = "id_dao_dien")
    )
    private Set<Director> daoDienList;

    @ManyToMany
    @JoinTable(
            name = "phim_dien_vien",
            joinColumns = @JoinColumn(name = "id_phim"),
            inverseJoinColumns = @JoinColumn(name = "id_dien_vien")
    )
    private Set<Actor> dienVienList;


    // Thêm trường cho soft delete
    @Column(name = "da_xoa", nullable = false)
    private Boolean daXoa = false;

    @Column(name = "ngay_xoa")
    private LocalDateTime ngayXoa;

    // Relationships
    @OneToMany(mappedBy = "phim", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ShowTime> lichChieu;

    @OneToMany(mappedBy = "phim", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MovieReview> danhGia;

    // Helper methods for soft delete
    public void softDelete() {
        this.daXoa = true;
        this.ngayXoa = LocalDateTime.now();
        this.ngayCapNhat = LocalDateTime.now();
    }

    public void restore() {
        this.daXoa = false;
        this.ngayXoa = null;
        this.ngayCapNhat = LocalDateTime.now();
    }

//    public boolean isDeleted() {
//        return this.daXoa != null && this.daXoa;
//    }

//    @PrePersist
//    protected void onCreate() {
//        ngayTao = LocalDateTime.now();
//        ngayCapNhat = LocalDateTime.now();
//        if (daXoa == null) {
//            daXoa = false;
//        }
//    }

    @PreUpdate
    protected void onUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }


}
