package org.example.cinema_reservation_system.controller.review;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.dto.reviewdto.ReviewImageRequestDto;
import org.example.cinema_reservation_system.dto.reviewdto.ReviewImageResponseDto;
import org.example.cinema_reservation_system.service.review.ReviewImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/review-images")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReviewImageController {
    
    private final ReviewImageService reviewImageService;
    
    /**
     * Thêm hình ảnh cho đánh giá phim
     */
    @PostMapping
    public ResponseEntity<ReviewImageResponseDto> addReviewImage(@Valid @RequestBody ReviewImageRequestDto requestDto) {
        ReviewImageResponseDto response = reviewImageService.addReviewImage(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Lấy tất cả hình ảnh của một đánh giá phim
     */
    @GetMapping("/review/{reviewId}")
    public ResponseEntity<List<ReviewImageResponseDto>> getReviewImages(@PathVariable Long reviewId) {
        List<ReviewImageResponseDto> images = reviewImageService.getReviewImages(reviewId);
        return ResponseEntity.ok(images);
    }
    
    /**
     * Lấy hình ảnh theo ID
     */
    @GetMapping("/{imageId}")
    public ResponseEntity<ReviewImageResponseDto> getReviewImageById(@PathVariable Long imageId) {
        ReviewImageResponseDto image = reviewImageService.getReviewImageById(imageId);
        return ResponseEntity.ok(image);
    }
    
    /**
     * Cập nhật hình ảnh
     */
    @PutMapping("/{imageId}")
    public ResponseEntity<ReviewImageResponseDto> updateReviewImage(
            @PathVariable Long imageId,
            @Valid @RequestBody ReviewImageRequestDto requestDto) {
        ReviewImageResponseDto response = reviewImageService.updateReviewImage(imageId, requestDto);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Xóa hình ảnh
     */
    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteReviewImage(@PathVariable Long imageId) {
        reviewImageService.deleteReviewImage(imageId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Xóa tất cả hình ảnh của một đánh giá phim
     */
    @DeleteMapping("/review/{reviewId}")
    public ResponseEntity<Void> deleteAllReviewImages(@PathVariable Long reviewId) {
        reviewImageService.deleteAllReviewImages(reviewId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Đếm số hình ảnh của một đánh giá phim
     */
    @GetMapping("/review/{reviewId}/count")
    public ResponseEntity<Long> countReviewImages(@PathVariable Long reviewId) {
        long count = reviewImageService.countReviewImages(reviewId);
        return ResponseEntity.ok(count);
    }
}

