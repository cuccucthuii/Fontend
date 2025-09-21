package org.example.cinema_reservation_system.repository.promotion;

import org.example.cinema_reservation_system.entity.PromotionCampaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PromotionCampaignRepository extends JpaRepository<PromotionCampaign, Integer> {
    
    // Tìm chiến dịch theo loại
    List<PromotionCampaign> findByLoaiChienDich(PromotionCampaign.LoaiChienDich loaiChienDich);
    
    // Tìm chiến dịch theo trạng thái
    List<PromotionCampaign> findByTrangThai(PromotionCampaign.TrangThaiChienDich trangThai);
    
    // Tìm chiến dịch đang hoạt động
    List<PromotionCampaign> findByTrangThaiOrderByUuTienAsc(PromotionCampaign.TrangThaiChienDich trangThai);
    
    // Tìm chiến dịch theo thời gian
    @Query("SELECT c FROM PromotionCampaign c WHERE c.ngayBatDau <= :ngayHienTai AND c.ngayKetThuc >= :ngayHienTai")
    List<PromotionCampaign> findByThoiGianHoatDong(@Param("ngayHienTai") LocalDateTime ngayHienTai);
    
    // Tìm chiến dịch theo khoảng thời gian
    @Query("SELECT c FROM PromotionCampaign c WHERE (c.ngayBatDau BETWEEN :tuNgay AND :denNgay) OR (c.ngayKetThuc BETWEEN :tuNgay AND :denNgay)")
    List<PromotionCampaign> findByKhoangThoiGian(@Param("tuNgay") LocalDateTime tuNgay, @Param("denNgay") LocalDateTime denNgay);
    
    // Tìm chiến dịch theo ưu tiên
    List<PromotionCampaign> findByUuTienGreaterThanOrderByUuTienAsc(Integer uuTien);
    
    // Tìm chiến dịch theo tên (tìm kiếm mờ)
    @Query("SELECT c FROM PromotionCampaign c WHERE LOWER(c.tenChienDich) LIKE LOWER(CONCAT('%', :tenChienDich, '%'))")
    List<PromotionCampaign> findByTenChienDichContainingIgnoreCase(@Param("tenChienDich") String tenChienDich);
    
    // Đếm số chiến dịch theo trạng thái
    long countByTrangThai(PromotionCampaign.TrangThaiChienDich trangThai);
    
    // Tìm chiến dịch có banner
    List<PromotionCampaign> findByHinhAnhBannerIsNotNull();
    
    // Tìm chiến dịch theo màu sắc
    List<PromotionCampaign> findByMauSac(String mauSac);
}













































