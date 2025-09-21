package org.example.cinema_reservation_system.repository.review;

import org.example.cinema_reservation_system.entity.ReviewReply;
import org.example.cinema_reservation_system.utils.enums.TrangThai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewReplyRepository extends JpaRepository<ReviewReply, Long> {
    
    /**
     * Tìm tất cả phản hồi của một đánh giá phim
     */
    @Query("SELECT rr FROM ReviewReply rr WHERE rr.movieReview.idDanhGia = :reviewId ORDER BY rr.repliedAt DESC")
    List<ReviewReply> findByMovieReviewIdOrderByRepliedAtDesc(@Param("reviewId") Long reviewId);
    
    /**
     * Tìm phản hồi theo trạng thái
     */
    @Query("SELECT rr FROM ReviewReply rr WHERE rr.status = :status ORDER BY rr.repliedAt DESC")
    List<ReviewReply> findByStatusOrderByRepliedAtDesc(@Param("status") TrangThai status);
    
    /**
     * Tìm phản hồi của một nhân viên
     */
    @Query("SELECT rr FROM ReviewReply rr WHERE rr.employee.idNhanVien = :employeeId ORDER BY rr.repliedAt DESC")
    List<ReviewReply> findByEmployeeIdOrderByRepliedAtDesc(@Param("employeeId") Long employeeId);
    
    /**
     * Đếm số phản hồi của một đánh giá phim
     */
    @Query("SELECT COUNT(rr) FROM ReviewReply rr WHERE rr.movieReview.idDanhGia = :reviewId")
    long countByMovieReviewId(@Param("reviewId") Long reviewId);
    
    /**
     * Xóa tất cả phản hồi của một đánh giá phim
     */
    @Query("DELETE FROM ReviewReply rr WHERE rr.movieReview.idDanhGia = :reviewId")
    void deleteByMovieReviewId(@Param("reviewId") Long reviewId);
    
    /**
     * Tìm phản hồi theo đánh giá và nhân viên
     */
    @Query("SELECT rr FROM ReviewReply rr WHERE rr.movieReview.idDanhGia = :reviewId AND rr.employee.idNhanVien = :employeeId")
    List<ReviewReply> findByReviewIdAndEmployeeId(@Param("reviewId") Long reviewId, @Param("employeeId") Long employeeId);
}

