package org.example.cinema_reservation_system.service.utility;

import org.example.cinema_reservation_system.entity.Promotion;
import org.example.cinema_reservation_system.entity.PromotionCampaign;
import org.example.cinema_reservation_system.entity.Customer;
import org.example.cinema_reservation_system.entity.Movie;
import org.example.cinema_reservation_system.entity.Theater;
import org.example.cinema_reservation_system.entity.FoodCombo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PromotionService {
    
    // =====================================================
    // QUẢN LÝ CHIẾN DỊCH KHuyẾN MÃI
    // =====================================================
    
    // CRUD Chiến dịch
    List<PromotionCampaign> getAllChienDich();
    Optional<PromotionCampaign> getChienDichById(Integer idChienDich);
    PromotionCampaign createChienDich(PromotionCampaign chienDich);
    PromotionCampaign updateChienDich(Integer idChienDich, PromotionCampaign chienDich);
    void deleteChienDich(Integer idChienDich);
    
    // Tìm kiếm chiến dịch
    List<PromotionCampaign> getChienDichByLoai(PromotionCampaign.LoaiChienDich loaiChienDich);
    List<PromotionCampaign> getChienDichDangHoatDong();
    List<PromotionCampaign> getChienDichTheoThoiGian(LocalDateTime tuNgay, LocalDateTime denNgay);
    
    // =====================================================
    // QUẢN LÝ KHuyẾN MÃI CHI TIẾT
    // =====================================================
    
    // CRUD Khuyến mãi
    List<Promotion> getAllKhuyenMai();
    Optional<Promotion> getKhuyenMaiById(Integer idKhuyenMai);
    Promotion createKhuyenMai(Promotion khuyenMai);
    Promotion updateKhuyenMai(Integer idKhuyenMai, Promotion khuyenMai);
    void deleteKhuyenMai(Integer idKhuyenMai);
    
    // Tìm kiếm khuyến mãi
    List<Promotion> getKhuyenMaiByChienDich(Integer idChienDich);
    List<Promotion> getKhuyenMaiDangHoatDong();
    List<Promotion> getKhuyenMaiByLoai(Promotion.LoaiGiam loaiGiam);
    
    // =====================================================
    // TÍNH TOÁN VÀ ÁP DỤNG KHuyẾN MÃI
    // =====================================================
    
    // Tính toán khuyến mãi cho hóa đơn
    List<Promotion> tinhToanKhuyenMaiChoHoaDon(
        Customer khachHang, 
        BigDecimal tongTien, 
        List<Movie> danhSachPhim,
        List<Theater> danhSachRap,
        List<FoodCombo> danhSachCombo
    );
    
    // Tính tiền giảm từ khuyến mãi
    BigDecimal tinhTienGiamTuKhuyenMai(Promotion khuyenMai, BigDecimal tongTien);
    
    // Kiểm tra điều kiện áp dụng khuyến mãi
    boolean kiemTraDieuKienKhuyenMai(Promotion khuyenMai, Customer khachHang, BigDecimal tongTien);
    
    // Áp dụng khuyến mãi cho hóa đơn
    void apDungKhuyenMaiChoHoaDon(Integer idHoaDon, Integer idKhuyenMai, BigDecimal soTienGiam);
    
    // =====================================================
    // KHuyẾN MÃI THEO ĐỐI TƯỢNG
    // =====================================================
    
    // Khuyến mãi sinh viên
    List<Promotion> getKhuyenMaiSinhVien();
    
    // Khuyến mãi VIP
    List<Promotion> getKhuyenMaiVIP();
    
    // Khuyến mãi sinh nhật
    List<Promotion> getKhuyenMaiSinhNhat(Customer khachHang);
    
    // Khuyến mãi khách hàng mới
    List<Promotion> getKhuyenMaiKhachHangMoi(Customer khachHang);
    
    // =====================================================
    // KHuyẾN MÃI THEO THỜI GIAN
    // =====================================================
    
    // Flash Sale
    List<Promotion> getFlashSaleDangHoatDong();
    
    // Khuyến mãi cuối tuần
    List<Promotion> getKhuyenMaiCuoiTuan();
    
    // Khuyến mãi theo giờ
    List<Promotion> getKhuyenMaiTheoGio();
    
    
    // =====================================================
    // THỐNG KÊ VÀ BÁO CÁO
    // =====================================================
    
    // Thống kê khuyến mãi
    BigDecimal getTongTienGiamTheoThoiGian(LocalDateTime tuNgay, LocalDateTime denNgay);
    Long getSoLuongKhuyenMaiSuDungTheoThoiGian(LocalDateTime tuNgay, LocalDateTime denNgay);
    
    // Báo cáo hiệu quả khuyến mãi
    List<Object[]> getBaoCaoHieuQuaKhuyenMai(LocalDateTime tuNgay, LocalDateTime denNgay);
    
    // =====================================================
    // QUẢN LÝ LỊCH SỬ
    // =====================================================
    
    // Lịch sử sử dụng khuyến mãi
    List<Object[]> getLichSuSuDungKhuyenMai(Integer idKhachHang);
    List<Object[]> getLichSuSuDungKhuyenMaiTheoThoiGian(LocalDateTime tuNgay, LocalDateTime denNgay);
}


