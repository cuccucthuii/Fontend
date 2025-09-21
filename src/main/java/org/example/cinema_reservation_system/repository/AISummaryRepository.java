package org.example.cinema_reservation_system.repository;

import org.example.cinema_reservation_system.entity.AISummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AISummaryRepository extends JpaRepository<AISummary, Long> {
    
    /**
     * Tìm AI summary theo phim
     */
    Optional<AISummary> findByPhimIdPhim(Integer movieId);
    
    /**
     * Tìm AI summary theo trạng thái
     */
    List<AISummary> findByTrangThai(String status);
    
    /**
     * Tìm AI summary theo phim và trạng thái
     */
    Optional<AISummary> findByPhimIdPhimAndTrangThai(Integer movieId, String status);
    
    /**
     * Đếm số AI summary theo trạng thái
     */
    long countByTrangThai(String status);
    
    /**
     * Tìm AI summary có nhiều lượt thích nhất
     */
    @Query("SELECT a FROM AISummary a ORDER BY a.soLuotThich DESC")
    List<AISummary> findTopBySoLuotThich();
    
    /**
     * Tìm AI summary theo phạm vi thời gian
     */
    @Query("SELECT a FROM AISummary a WHERE a.createdAt BETWEEN :startDate AND :endDate")
    List<AISummary> findByCreatedAtBetween(@Param("startDate") java.time.LocalDateTime startDate, 
                                          @Param("endDate") java.time.LocalDateTime endDate);
}

















































