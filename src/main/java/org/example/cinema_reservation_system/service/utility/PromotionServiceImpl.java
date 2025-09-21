package org.example.cinema_reservation_system.service.utility;

import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.entity.*;
import org.example.cinema_reservation_system.repository.promotion.PromotionRepository;
import org.example.cinema_reservation_system.repository.promotion.PromotionCampaignRepository;
import org.example.cinema_reservation_system.service.utility.PromotionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PromotionServiceImpl implements PromotionService {
    
    private final PromotionRepository promotionRepository;
    private final PromotionCampaignRepository campaignRepository;
    
    // =====================================================
    // QUẢN LÝ CHIẾN DỊCH KHUYẾN MÃI
    // =====================================================
    
    @Override
    @Transactional(readOnly = true)
    public List<PromotionCampaign> getAllChienDich() {
        return campaignRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<PromotionCampaign> getChienDichById(Integer idChienDich) {
        return campaignRepository.findById(idChienDich);
    }
    
    @Override
    public PromotionCampaign createChienDich(PromotionCampaign chienDich) {
        return campaignRepository.save(chienDich);
    }
    
    @Override
    public PromotionCampaign updateChienDich(Integer idChienDich, PromotionCampaign chienDich) {
        PromotionCampaign existing = campaignRepository.findById(idChienDich)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chiến dịch với ID: " + idChienDich));
        
        existing.setTenChienDich(chienDich.getTenChienDich());
        existing.setMoTa(chienDich.getMoTa());
        existing.setLoaiChienDich(chienDich.getLoaiChienDich());
        existing.setNgayBatDau(chienDich.getNgayBatDau());
        existing.setNgayKetThuc(chienDich.getNgayKetThuc());
        existing.setTrangThai(chienDich.getTrangThai());
        existing.setHinhAnhBanner(chienDich.getHinhAnhBanner());
        existing.setMauSac(chienDich.getMauSac());
        existing.setUuTien(chienDich.getUuTien());
        
        return campaignRepository.save(existing);
    }
    
    @Override
    public void deleteChienDich(Integer idChienDich) {
        campaignRepository.deleteById(idChienDich);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PromotionCampaign> getChienDichByLoai(PromotionCampaign.LoaiChienDich loaiChienDich) {
        return campaignRepository.findByLoaiChienDich(loaiChienDich);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PromotionCampaign> getChienDichDangHoatDong() {
        return campaignRepository.findByThoiGianHoatDong(LocalDateTime.now());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PromotionCampaign> getChienDichTheoThoiGian(LocalDateTime tuNgay, LocalDateTime denNgay) {
        return campaignRepository.findByKhoangThoiGian(tuNgay, denNgay);
    }
    
    // =====================================================
    // QUẢN LÝ KHUYẾN MÃI CHI TIẾT
    // =====================================================
    
    @Override
    @Transactional(readOnly = true)
    public List<Promotion> getAllKhuyenMai() {
        return promotionRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Promotion> getKhuyenMaiById(Integer idKhuyenMai) {
        return promotionRepository.findById(idKhuyenMai);
    }
    
    @Override
    public Promotion createKhuyenMai(Promotion khuyenMai) {
        return promotionRepository.save(khuyenMai);
    }
    
    @Override
    public Promotion updateKhuyenMai(Integer idKhuyenMai, Promotion khuyenMai) {
        Promotion existing = promotionRepository.findById(idKhuyenMai)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khuyến mãi với ID: " + idKhuyenMai));
        
        existing.setTenKhuyenMai(khuyenMai.getTenKhuyenMai());
        existing.setMoTa(khuyenMai.getMoTa());
        existing.setLoaiGiam(khuyenMai.getLoaiGiam());
        existing.setGiaTriGiam(khuyenMai.getGiaTriGiam());
        existing.setPhanTramGiam(khuyenMai.getPhanTramGiam());
        existing.setMucGiamToiDa(khuyenMai.getMucGiamToiDa());
        existing.setDieuKienApDung(khuyenMai.getDieuKienApDung());
        existing.setSoLuongToiDa(khuyenMai.getSoLuongToiDa());
        existing.setTrangThai(khuyenMai.getTrangThai());
        
        return promotionRepository.save(existing);
    }
    
    @Override
    public void deleteKhuyenMai(Integer idKhuyenMai) {
        promotionRepository.deleteById(idKhuyenMai);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Promotion> getKhuyenMaiByChienDich(Integer idChienDich) {
        PromotionCampaign chienDich = campaignRepository.findById(idChienDich)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chiến dịch với ID: " + idChienDich));
        return promotionRepository.findByChienDich(chienDich);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Promotion> getKhuyenMaiDangHoatDong() {
        return promotionRepository.findByTrangThai(Promotion.TrangThaiKhuyenMai.HOAT_DONG);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Promotion> getKhuyenMaiByLoai(Promotion.LoaiGiam loaiGiam) {
        return promotionRepository.findByLoaiGiam(loaiGiam);
    }
    
    // =====================================================
    // TÍNH TOÁN VÀ ÁP DỤNG KHUYẾN MÃI
    // =====================================================
    
    @Override
    @Transactional(readOnly = true)
    public List<Promotion> tinhToanKhuyenMaiChoHoaDon(
            Customer khachHang, 
            BigDecimal tongTien, 
            List<Movie> danhSachPhim,
            List<Theater> danhSachRap,
            List<FoodCombo> danhSachCombo) {
        
        List<Promotion> tatCaKhuyenMai = promotionRepository.findAvailablePromotions();
        
        return tatCaKhuyenMai.stream()
                .filter(km -> kiemTraDieuKienKhuyenMai(km, khachHang, tongTien))
                .filter(km -> kiemTraKhuyenMaiPhuHop(km, danhSachPhim, danhSachRap, danhSachCombo))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal tinhTienGiamTuKhuyenMai(Promotion khuyenMai, BigDecimal tongTien) {
        return khuyenMai.tinhTienGiam(tongTien);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean kiemTraDieuKienKhuyenMai(Promotion khuyenMai, Customer khachHang, BigDecimal tongTien) {
        // Kiểm tra trạng thái khuyến mãi
        if (!khuyenMai.conKhuyenMai()) {
            return false;
        }
        
        // Kiểm tra điều kiện áp dụng
        if (khuyenMai.getDieuKienApDung() != null && !khuyenMai.getDieuKienApDung().isEmpty()) {
            // Có thể thêm logic kiểm tra điều kiện phức tạp ở đây
            if (khuyenMai.getDieuKienApDung().contains("sinh viên") && 
                !"SINH_VIEN".equalsIgnoreCase(khachHang.getHangThanhVien())) {
                return false;
            }
        }
        
        return true;
    }
    
    private boolean kiemTraKhuyenMaiPhuHop(Promotion khuyenMai, 
                                           List<Movie> danhSachPhim,
                                           List<Theater> danhSachRap, 
                                           List<FoodCombo> danhSachCombo) {
        
        
        return true;
    }
    
    @Override
    public void apDungKhuyenMaiChoHoaDon(Integer idHoaDon, Integer idKhuyenMai, BigDecimal soTienGiam) {
        // Cập nhật số lượng đã sử dụng
        Promotion khuyenMai = promotionRepository.findById(idKhuyenMai)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khuyến mãi"));
        
        khuyenMai.setSoLuongDaSuDung(khuyenMai.getSoLuongDaSuDung() + 1);
        promotionRepository.save(khuyenMai);
        
        // TODO: Tạo lịch sử sử dụng khuyến mãi
    }
    
    // =====================================================
    // KHUYẾN MÃI THEO ĐỐI TƯỢNG
    // =====================================================
    
    @Override
    @Transactional(readOnly = true)
    public List<Promotion> getKhuyenMaiSinhVien() {
        return promotionRepository.findByDieuKienApDung("sinh viên");
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Promotion> getKhuyenMaiVIP() {
        return promotionRepository.findByDieuKienApDung("VIP");
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Promotion> getKhuyenMaiSinhNhat(Customer khachHang) {
        // TODO: Implement logic khuyến mãi sinh nhật
        return List.of();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Promotion> getKhuyenMaiKhachHangMoi(Customer khachHang) {
        // TODO: Implement logic khuyến mãi khách hàng mới
        return List.of();
    }
    
    // =====================================================
    // KHUYẾN MÃI THEO THỜI GIAN
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<Promotion> getFlashSaleDangHoatDong() {
        return promotionRepository.findByTrangThaiOrderByNgayTaoDesc(Promotion.TrangThaiKhuyenMai.HOAT_DONG);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Promotion> getKhuyenMaiCuoiTuan() {
        // Giả định khuyến mãi cuối tuần có điều kiện chứa từ khóa 'cuoi_tuan' trong dieuKienApDung
        return promotionRepository.findByDieuKienApDung("cuoi_tuan");
    }

    @Override
    @Transactional(readOnly = true)
    public List<Promotion> getKhuyenMaiTheoGio() {
        // Giả định khuyến mãi theo giờ có điều kiện chứa từ khóa 'theo_gio' trong dieuKienApDung
        return promotionRepository.findByDieuKienApDung("theo_gio");
    }
    
    
    // =====================================================
    // THỐNG KÊ VÀ BÁO CÁO
    // =====================================================
    
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTongTienGiamTheoThoiGian(LocalDateTime tuNgay, LocalDateTime denNgay) {
        // Tổng hợp tạm thời: giả sử lấy tất cả khuyến mãi tạo trong khoảng thời gian và cộng giá trị giảm tối đa
        // Có thể thay bằng truy vấn/bảng lịch sử sử dụng thực tế nếu có
        List<Promotion> list = promotionRepository.findByThoiGian(tuNgay, denNgay);
        return list.stream()
                .map(p -> p.getGiaTriGiam() != null ? p.getGiaTriGiam() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getSoLuongKhuyenMaiSuDungTheoThoiGian(LocalDateTime tuNgay, LocalDateTime denNgay) {
        // Tạm tính: đếm số khuyến mãi tạo trong khoảng; nếu có bảng lịch sử sử dụng, nên đổi sang đếm theo lịch sử
        return (long) promotionRepository.findByThoiGian(tuNgay, denNgay).size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getBaoCaoHieuQuaKhuyenMai(LocalDateTime tuNgay, LocalDateTime denNgay) {
        // Placeholder: trả rỗng; có thể bổ sung sau bằng native query/DTO report
        return List.of();
    }

    // =====================================================
    // QUẢN LÝ LỊCH SỬ
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getLichSuSuDungKhuyenMai(Integer idKhachHang) {
        // Chưa có bảng lịch sử => trả danh sách rỗng (placeholder)
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getLichSuSuDungKhuyenMaiTheoThoiGian(LocalDateTime tuNgay, LocalDateTime denNgay) {
        // Chưa có bảng lịch sử => trả danh sách rỗng (placeholder)
        return List.of();
    }
}
