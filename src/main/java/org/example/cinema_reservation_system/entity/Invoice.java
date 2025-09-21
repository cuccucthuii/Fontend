package org.example.cinema_reservation_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.cinema_reservation_system.utils.enums.TrangThai;
import org.example.cinema_reservation_system.utils.enums.TrangThaiHoaDon;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "hoa_don")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hoa_don")
    private Integer idHoaDon;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang", nullable = true)
    private Customer khachHang;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nhan_vien")
    private Staff nhanVien;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_voucher")
    private Voucher voucher;
    
    @Column(name = "ten_hoa_don")
    private String tenHoaDon;
    
    @Column(name = "tong_tien")
    private Double tongTien;
    
    @Column(name = "tien_giam")
    private Double tienGiam;
    
    @Column(name = "ngay_dat")
    private LocalDate ngayDat;
    
    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;
    
    @Column(name = "loai_hoa_don")
    private String loaiHoaDon;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private TrangThaiHoaDon trangThai;
    
    @Column(name = "ma_dat_ve")
    private String maDatVe;
    
    @Column(name = "ma_giao_dich")
    private String maGiaoDich;
    
    // ma_giao_dich_vnpay đã loại bỏ trong DB, dùng ma_giao_dich chung
    
    @Column(name = "ten_khach_hang")
    private String tenKhachHang;
    
    @Column(name = "so_dien_thoai")
    private String soDienThoai;
    
    @Column(name = "trang_thai_email")
    private String trangThaiEmail;
    
    @Column(name = "ngay_gui_email")
    private LocalDateTime ngayGuiEmail;
    
    @Column(name = "phuong_thuc_thanh_toan")
    private String phuongThucThanhToan;

    // Trạng thái thanh toán của hóa đơn (CHO_THANH_TOAN/HOAN_THANH/DA_HUY)
    @Column(name = "trang_thai_thanh_toan")
    private String trangThaiThanhToan;
    
    @Column(name = "ghi_chu")
    private String ghiChu;
    
    // Business methods
    public boolean coTheHuy() {
        return TrangThaiHoaDon.CHO_THANH_TOAN.equals(this.trangThai);
    }
    
    public boolean daThanhToan() {
        return TrangThaiHoaDon.DA_THANH_TOAN.equals(this.trangThai);
    }
}
