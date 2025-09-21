package org.example.cinema_reservation_system.repository.statistics;

import org.example.cinema_reservation_system.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserStatisticsRepository extends JpaRepository<Customer, Integer> {
    
    // Đếm số vé đã mua
    @Query(value = "SELECT COUNT(*) FROM ve_phim WHERE id_khach_hang = :customerId AND trang_thai = 'DA_BAN'", nativeQuery = true)
    Integer countTicketsPurchased(@Param("customerId") Integer customerId);
    
    // Đếm số phim đã xem (dựa trên đánh giá có da_xem_phim = true)
    @Query(value = "SELECT COUNT(DISTINCT id_phim) FROM danh_gia_phim WHERE id_khach_hang = :customerId AND da_xem_phim = true", nativeQuery = true)
    Integer countMoviesWatched(@Param("customerId") Integer customerId);
    
    // Đếm số đánh giá đã viết
    @Query(value = "SELECT COUNT(*) FROM danh_gia_phim WHERE id_khach_hang = :customerId AND trang_thai = 'HOAT_DONG'", nativeQuery = true)
    Integer countReviews(@Param("customerId") Integer customerId);
    
    // Đếm số bình luận (phản hồi) đã viết
    @Query(value = "SELECT COUNT(*) FROM review_replies WHERE id_khach_hang = :customerId", nativeQuery = true)
    Integer countComments(@Param("customerId") Integer customerId);
    
    // Lấy điểm tích lũy hiện tại
    @Query(value = "SELECT diem_tich_luy FROM khach_hang WHERE id_khach_hang = :customerId", nativeQuery = true)
    Integer getCurrentPoints(@Param("customerId") Integer customerId);
    
    // Điểm đánh giá trung bình
    @Query(value = "SELECT COALESCE(AVG(so_sao), 0.0) FROM danh_gia_phim WHERE id_khach_hang = :customerId AND trang_thai = 'HOAT_DONG'", nativeQuery = true)
    Double getAverageRating(@Param("customerId") Integer customerId);
    
    // Phim đã xem gần đây (5 phim gần nhất)
    @Query(value = """
        SELECT DISTINCT p.id_phim, p.ten_phim, p.ngay_phat_hanh, dg.ngay_xem_phim, 
               dg.rap_chieu, dg.loai_ve, dg.so_sao, dg.noi_dung_danh_gia
        FROM danh_gia_phim dg
        JOIN phim p ON dg.id_phim = p.id_phim
        WHERE dg.id_khach_hang = :customerId AND dg.da_xem_phim = true
        ORDER BY dg.ngay_xem_phim DESC
        LIMIT 5
        """, nativeQuery = true)
    List<Object[]> getRecentMovies(@Param("customerId") Integer customerId);
    
    // Số ngày tham gia hệ thống
    @Query(value = "SELECT EXTRACT(DAY FROM (CURRENT_DATE - ngay_tao)) FROM khach_hang WHERE id_khach_hang = :customerId", nativeQuery = true)
    Integer getDaysSinceRegistration(@Param("customerId") Integer customerId);
}
