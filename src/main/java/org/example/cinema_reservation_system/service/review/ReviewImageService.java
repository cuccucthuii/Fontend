package org.example.cinema_reservation_system.service.review;

import org.example.cinema_reservation_system.dto.reviewdto.ReviewImageRequestDto;
import org.example.cinema_reservation_system.dto.reviewdto.ReviewImageResponseDto;

import java.util.List;

public interface ReviewImageService {
    
    /**
     * Thêm h/ả cho đánh giá phim
     */
    ReviewImageResponseDto addReviewImage(ReviewImageRequestDto requestDto);
    
    /**
     * Lấy full h/ả của 1 đgiá phim
     */
    List<ReviewImageResponseDto> getReviewImages(Long reviewId);
    
    /**
     * Lấy h/ả theo ID
     */
    ReviewImageResponseDto getReviewImageById(Long imageId);
    
    /**
     * Cập nhật h/ả
     */
    ReviewImageResponseDto updateReviewImage(Long imageId, ReviewImageRequestDto requestDto);
    
    /**
     * Xóa h/ả
     */
    void deleteReviewImage(Long imageId);
    
    /**
     * Xóa full h/ả của 1 đgiá phim
     */
    void deleteAllReviewImages(Long reviewId);
    
    /**
     * Đếm số h/ả của một đgiá phim
     */
    long countReviewImages(Long reviewId);
}

