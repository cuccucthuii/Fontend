package org.example.cinema_reservation_system.controller.utility;

import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.entity.Promotion;
import org.example.cinema_reservation_system.entity.PromotionCampaign;
import org.example.cinema_reservation_system.service.utility.PromotionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PromotionController {
    
    private final PromotionService promotionService;
    
    // =====================================================
    // QUẢN LÝ CHIẾN DỊCH KHUYẾN MÃI
    // =====================================================
    
    @GetMapping("/campaigns")
    public ResponseEntity<List<PromotionCampaign>> getAllChienDich() {
        List<PromotionCampaign> chienDichs = promotionService.getAllChienDich();
        return ResponseEntity.ok(chienDichs);
    }
    
    @GetMapping("/campaigns/{id}")
    public ResponseEntity<PromotionCampaign> getChienDichById(@PathVariable Integer id) {
        return promotionService.getChienDichById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/campaigns")
    public ResponseEntity<PromotionCampaign> createChienDich(@RequestBody PromotionCampaign chienDich) {
        PromotionCampaign created = promotionService.createChienDich(chienDich);
        return ResponseEntity.ok(created);
    }
    
    @PutMapping("/campaigns/{id}")
    public ResponseEntity<PromotionCampaign> updateChienDich(
            @PathVariable Integer id, 
            @RequestBody PromotionCampaign chienDich) {
        PromotionCampaign updated = promotionService.updateChienDich(id, chienDich);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/campaigns/{id}")
    public ResponseEntity<Void> deleteChienDich(@PathVariable Integer id) {
        promotionService.deleteChienDich(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/campaigns/type/{loaiChienDich}")
    public ResponseEntity<List<PromotionCampaign>> getChienDichByLoai(
            @PathVariable PromotionCampaign.LoaiChienDich loaiChienDich) {
        List<PromotionCampaign> chienDichs = promotionService.getChienDichByLoai(loaiChienDich);
        return ResponseEntity.ok(chienDichs);
    }
    
    @GetMapping("/campaigns/active")
    public ResponseEntity<List<PromotionCampaign>> getChienDichDangHoatDong() {
        List<PromotionCampaign> chienDichs = promotionService.getChienDichDangHoatDong();
        return ResponseEntity.ok(chienDichs);
    }
    
    @GetMapping("/campaigns/time-range")
    public ResponseEntity<List<PromotionCampaign>> getChienDichTheoThoiGian(
            @RequestParam LocalDateTime tuNgay,
            @RequestParam LocalDateTime denNgay) {
        List<PromotionCampaign> chienDichs = promotionService.getChienDichTheoThoiGian(tuNgay, denNgay);
        return ResponseEntity.ok(chienDichs);
    }
    
    // =====================================================
    // QUẢN LÝ KHUYẾN MÃI CHI TIẾT
    // =====================================================
    
    @GetMapping
    public ResponseEntity<List<Promotion>> getAllKhuyenMai() {
        List<Promotion> khuyenMais = promotionService.getAllKhuyenMai();
        return ResponseEntity.ok(khuyenMais);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Promotion> getKhuyenMaiById(@PathVariable Integer id) {
        return promotionService.getKhuyenMaiById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Promotion> createKhuyenMai(@RequestBody Promotion khuyenMai) {
        Promotion created = promotionService.createKhuyenMai(khuyenMai);
        return ResponseEntity.ok(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Promotion> updateKhuyenMai(
            @PathVariable Integer id, 
            @RequestBody Promotion khuyenMai) {
        Promotion updated = promotionService.updateKhuyenMai(id, khuyenMai);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKhuyenMai(@PathVariable Integer id) {
        promotionService.deleteKhuyenMai(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/campaign/{idChienDich}")
    public ResponseEntity<List<Promotion>> getKhuyenMaiByChienDich(@PathVariable Integer idChienDich) {
        List<Promotion> khuyenMais = promotionService.getKhuyenMaiByChienDich(idChienDich);
        return ResponseEntity.ok(khuyenMais);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<Promotion>> getKhuyenMaiDangHoatDong() {
        List<Promotion> khuyenMais = promotionService.getKhuyenMaiDangHoatDong();
        return ResponseEntity.ok(khuyenMais);
    }
    
    @GetMapping("/type/{loaiGiam}")
    public ResponseEntity<List<Promotion>> getKhuyenMaiByLoai(@PathVariable Promotion.LoaiGiam loaiGiam) {
        List<Promotion> khuyenMais = promotionService.getKhuyenMaiByLoai(loaiGiam);
        return ResponseEntity.ok(khuyenMais);
    }
    
    // =====================================================
    // TÍNH TOÁN VÀ ÁP DỤNG KHUYẾN MÃI
    // =====================================================
    
    @PostMapping("/calculate")
    public ResponseEntity<List<Promotion>> tinhToanKhuyenMaiChoHoaDon(
            @RequestBody KhuyenMaiRequest request) {
        List<Promotion> khuyenMais = promotionService.tinhToanKhuyenMaiChoHoaDon(
                request.getKhachHang(),
                request.getTongTien(),
                request.getDanhSachPhim(),
                request.getDanhSachRap(),
                request.getDanhSachCombo()
        );
        return ResponseEntity.ok(khuyenMais);
    }
    
    @PostMapping("/{id}/apply")
    public ResponseEntity<Void> apDungKhuyenMaiChoHoaDon(
            @PathVariable Integer id,
            @RequestParam Integer idHoaDon,
            @RequestParam BigDecimal soTienGiam) {
        promotionService.apDungKhuyenMaiChoHoaDon(idHoaDon, id, soTienGiam);
        return ResponseEntity.ok().build();
    }
    
    // =====================================================
    // KHUYẾN MÃI THEO ĐỐI TƯỢNG
    // =====================================================
    
    @GetMapping("/student")
    public ResponseEntity<List<Promotion>> getKhuyenMaiSinhVien() {
        List<Promotion> khuyenMais = promotionService.getKhuyenMaiSinhVien();
        return ResponseEntity.ok(khuyenMais);
    }
    
    @GetMapping("/vip")
    public ResponseEntity<List<Promotion>> getKhuyenMaiVIP() {
        List<Promotion> khuyenMais = promotionService.getKhuyenMaiVIP();
        return ResponseEntity.ok(khuyenMais);
    }
    
    @GetMapping("/birthday")
    public ResponseEntity<List<Promotion>> getKhuyenMaiSinhNhat(@RequestParam Integer idKhachHang) {
        // TODO: Implement customer lookup
        List<Promotion> khuyenMais = promotionService.getKhuyenMaiSinhNhat(null);
        return ResponseEntity.ok(khuyenMais);
    }
    
    @GetMapping("/new-customer")
    public ResponseEntity<List<Promotion>> getKhuyenMaiKhachHangMoi(@RequestParam Integer idKhachHang) {
        // TODO: Implement customer lookup
        List<Promotion> khuyenMais = promotionService.getKhuyenMaiKhachHangMoi(null);
        return ResponseEntity.ok(khuyenMais);
    }
    
    // =====================================================
    // KHUYẾN MÃI THEO THỜI GIAN
    // =====================================================
    
    @GetMapping("/flash-sale")
    public ResponseEntity<List<Promotion>> getFlashSaleDangHoatDong() {
        List<Promotion> khuyenMais = promotionService.getFlashSaleDangHoatDong();
        return ResponseEntity.ok(khuyenMais);
    }
    
    @GetMapping("/weekend")
    public ResponseEntity<List<Promotion>> getKhuyenMaiCuoiTuan() {
        List<Promotion> khuyenMais = promotionService.getKhuyenMaiCuoiTuan();
        return ResponseEntity.ok(khuyenMais);
    }
    
    @GetMapping("/hourly")
    public ResponseEntity<List<Promotion>> getKhuyenMaiTheoGio() {
        List<Promotion> khuyenMais = promotionService.getKhuyenMaiTheoGio();
        return ResponseEntity.ok(khuyenMais);
    }
    
    
    // =====================================================
    // THỐNG KÊ VÀ BÁO CÁO
    // =====================================================
    
    @GetMapping("/statistics/total-discount")
    public ResponseEntity<BigDecimal> getTongTienGiamTheoThoiGian(
            @RequestParam LocalDateTime tuNgay,
            @RequestParam LocalDateTime denNgay) {
        BigDecimal tongTienGiam = promotionService.getTongTienGiamTheoThoiGian(tuNgay, denNgay);
        return ResponseEntity.ok(tongTienGiam);
    }
    
    @GetMapping("/statistics/usage-count")
    public ResponseEntity<Long> getSoLuongKhuyenMaiSuDungTheoThoiGian(
            @RequestParam LocalDateTime tuNgay,
            @RequestParam LocalDateTime denNgay) {
        Long soLuong = promotionService.getSoLuongKhuyenMaiSuDungTheoThoiGian(tuNgay, denNgay);
        return ResponseEntity.ok(soLuong);
    }
    
    @GetMapping("/statistics/effectiveness")
    public ResponseEntity<List<Object[]>> getBaoCaoHieuQuaKhuyenMai(
            @RequestParam LocalDateTime tuNgay,
            @RequestParam LocalDateTime denNgay) {
        List<Object[]> baoCao = promotionService.getBaoCaoHieuQuaKhuyenMai(tuNgay, denNgay);
        return ResponseEntity.ok(baoCao);
    }
    
    // =====================================================
    // QUẢN LÝ LỊCH SỬ
    // =====================================================
    
    @GetMapping("/history/customer/{idKhachHang}")
    public ResponseEntity<List<Object[]>> getLichSuSuDungKhuyenMai(@PathVariable Integer idKhachHang) {
        List<Object[]> lichSu = promotionService.getLichSuSuDungKhuyenMai(idKhachHang);
        return ResponseEntity.ok(lichSu);
    }
    
    @GetMapping("/history/time-range")
    public ResponseEntity<List<Object[]>> getLichSuSuDungKhuyenMaiTheoThoiGian(
            @RequestParam LocalDateTime tuNgay,
            @RequestParam LocalDateTime denNgay) {
        List<Object[]> lichSu = promotionService.getLichSuSuDungKhuyenMaiTheoThoiGian(tuNgay, denNgay);
        return ResponseEntity.ok(lichSu);
    }
    
    // =====================================================
    // DTO CLASSES
    // =====================================================
    
    public static class KhuyenMaiRequest {
        private org.example.cinema_reservation_system.entity.Customer khachHang;
        private BigDecimal tongTien;
        private List<org.example.cinema_reservation_system.entity.Movie> danhSachPhim;
        private List<org.example.cinema_reservation_system.entity.Theater> danhSachRap;
        private List<org.example.cinema_reservation_system.entity.FoodCombo> danhSachCombo;
        
        // Getters and Setters
        public org.example.cinema_reservation_system.entity.Customer getKhachHang() { return khachHang; }
        public void setKhachHang(org.example.cinema_reservation_system.entity.Customer khachHang) { this.khachHang = khachHang; }
        
        public BigDecimal getTongTien() { return tongTien; }
        public void setTongTien(BigDecimal tongTien) { this.tongTien = tongTien; }
        
        public List<org.example.cinema_reservation_system.entity.Movie> getDanhSachPhim() { return danhSachPhim; }
        public void setDanhSachPhim(List<org.example.cinema_reservation_system.entity.Movie> danhSachPhim) { this.danhSachPhim = danhSachPhim; }
        
        public List<org.example.cinema_reservation_system.entity.Theater> getDanhSachRap() { return danhSachRap; }
        public void setDanhSachRap(List<org.example.cinema_reservation_system.entity.Theater> danhSachRap) { this.danhSachRap = danhSachRap; }
        
        public List<org.example.cinema_reservation_system.entity.FoodCombo> getDanhSachCombo() { return danhSachCombo; }
        public void setDanhSachCombo(List<org.example.cinema_reservation_system.entity.FoodCombo> danhSachCombo) { this.danhSachCombo = danhSachCombo; }
    }
}
