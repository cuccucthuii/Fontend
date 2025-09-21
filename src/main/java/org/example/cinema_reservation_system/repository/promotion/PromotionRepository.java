package org.example.cinema_reservation_system.repository.promotion;

import org.example.cinema_reservation_system.entity.Promotion;
import org.example.cinema_reservation_system.entity.PromotionCampaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
    
    // Tìm khuyến mãi theo chiến dịch
    List<Promotion> findByChienDich(PromotionCampaign chienDich);
    
    // Tìm khuyến mãi theo trạng thái
    List<Promotion> findByTrangThai(Promotion.TrangThaiKhuyenMai trangThai);
    
    // Tìm khuyến mãi đang hoạt động
    List<Promotion> findByTrangThaiOrderByNgayTaoDesc(Promotion.TrangThaiKhuyenMai trangThai);
    
    // Tìm khuyến mãi theo loại giảm
    List<Promotion> findByLoaiGiam(Promotion.LoaiGiam loaiGiam);

    // Tìm khuyến mãi theo thời gian
    @Query("SELECT p FROM Promotion p WHERE p.ngayTao BETWEEN :tuNgay AND :denNgay")
    List<Promotion> findByThoiGian(@Param("tuNgay") LocalDateTime tuNgay, @Param("denNgay") LocalDateTime denNgay);
    
    // Tìm khuyến mãi theo giá trị hóa đơn tối thiểu
    @Query("SELECT p FROM Promotion p WHERE p.dieuKienApDung LIKE %:keyword% AND p.trangThai = 'HOAT_DONG'")
    List<Promotion> findByDieuKienApDung(@Param("keyword") String keyword);
    
    // Đếm số khuyến mãi theo trạng thái
    long countByTrangThai(Promotion.TrangThaiKhuyenMai trangThai);
    
    // Tìm khuyến mãi có thể áp dụng cho hóa đơn
    @Query("SELECT p FROM Promotion p WHERE p.trangThai = 'HOAT_DONG' AND (p.soLuongToiDa = -1 OR p.soLuongDaSuDung < p.soLuongToiDa)")
    List<Promotion> findAvailablePromotions();
}





