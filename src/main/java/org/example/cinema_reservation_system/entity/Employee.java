package org.example.cinema_reservation_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "nhan_vien")
public class Employee {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id_nhan_vien")
        private Integer idNhanVien;

        @Column(name = "ten_nhan_vien", nullable = false, length = 100)
        private String tenNhanVien;

        @Column(name = "email", nullable = false, unique = true, length = 100)
        private String email;

        @Column(name = "so_dien_thoai", nullable = false, length = 15)
        private String soDienThoai;

        @Column(name = "ngay_sinh")
        private LocalDate ngaySinh;

        @Column(name = "gioi_tinh", length = 10)
        private String gioiTinh; // Nên validate ở service/controller: "Nam", "Nu", "Khac"

        @Column(name = "cccd", unique = true, length = 20)
        private String cccd;

        @Column(name = "trang_thai", length = 20)
        private String trangThai = "HOAT_DONG"; // "HOAT_DONG", "KHONG_HOAT_DONG"

        @Column(name = "ngay_vao_lam", nullable = false)
        private LocalDate ngayVaoLam;

        @Column(name = "chuc_vu", nullable = false, length = 50)
        private String chucVu; // "QuanLy", "ThuNgan", "BanVe"

        @Column(name = "luong_co_ban", precision = 15, scale = 2)
        private BigDecimal luongCoBan;

        @Column(name = "loai_hop_dong", length = 50)
        private String loaiHopDong; // "ThuViec", "ChinhThuc", "PartTime", "ThoiVu"

        @UpdateTimestamp
        @Column(name = "ngay_cap_nhat")
        private LocalDateTime ngayCapNhat;

        @Column(name = "ghi_chu", columnDefinition = "TEXT")
        private String ghiChu;

        @ManyToOne
        @JoinColumn(name = "id_rap_chieu", nullable = false)
        private Theater rapChieu; // Entity RapChieu phải có

        @Column(name = "anh_dai_dien", length = 255)
        private String anhDaiDien;

        @Column(name = "dia_chi", columnDefinition = "TEXT")
        private String diaChi;

        @Column(name = "so_lan_ban_ve")
        private Integer soLanBanVe = 0;

        @Column(name = "tong_doanh_thu_ban_ve", precision = 15, scale = 2)
        private BigDecimal tongDoanhThuBanVe = BigDecimal.ZERO;

        public boolean isHoatDong() {
                return "HOAT_DONG".equalsIgnoreCase(trangThai);
        }
}
