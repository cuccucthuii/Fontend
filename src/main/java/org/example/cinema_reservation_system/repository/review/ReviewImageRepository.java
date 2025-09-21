package org.example.cinema_reservation_system.repository.review;

import org.example.cinema_reservation_system.entity.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
    
    /**
     * Tìm tất cả hình ảnh của một đánh giá phim
     */
    @Query("SELECT ri FROM ReviewImage ri WHERE ri.movieReview.idDanhGia = :reviewId ORDER BY ri.createdAt DESC")
    List<ReviewImage> findByMovieReviewIdOrderByCreatedAtDesc(@Param("reviewId") Long reviewId);
    
    /**
     * Đếm số hình ảnh của một đánh giá phim
     */
    @Query("SELECT COUNT(ri) FROM ReviewImage ri WHERE ri.movieReview.idDanhGia = :reviewId")
    long countByMovieReviewId(@Param("reviewId") Long reviewId);
    
    /**
     * Xóa tất cả hình ảnh của một đánh giá phim
     */
    @Query("DELETE FROM ReviewImage ri WHERE ri.movieReview.idDanhGia = :reviewId")
    void deleteByMovieReviewId(@Param("reviewId") Long reviewId);
    
    /**
     * Tìm hình ảnh theo URL
     */
    @Query("SELECT ri FROM ReviewImage ri WHERE ri.imageUrl = :imageUrl")
    List<ReviewImage> findByImageUrl(@Param("imageUrl") String imageUrl);
}

