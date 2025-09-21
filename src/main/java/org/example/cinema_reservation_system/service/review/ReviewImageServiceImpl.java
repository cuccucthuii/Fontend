package org.example.cinema_reservation_system.service.review;

import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.dto.reviewdto.ReviewImageRequestDto;
import org.example.cinema_reservation_system.dto.reviewdto.ReviewImageResponseDto;
import org.example.cinema_reservation_system.entity.MovieReview;
import org.example.cinema_reservation_system.entity.ReviewImage;
import org.example.cinema_reservation_system.exception.BusinessException;
import org.example.cinema_reservation_system.repository.movie.MovieReviewRepository;
import org.example.cinema_reservation_system.repository.review.ReviewImageRepository;
import org.example.cinema_reservation_system.service.review.ReviewImageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewImageServiceImpl implements ReviewImageService {
    
    private final ReviewImageRepository reviewImageRepository;
    private final MovieReviewRepository movieReviewRepository;
    
    @Override
    public ReviewImageResponseDto addReviewImage(ReviewImageRequestDto requestDto) {
        // Kiểm tra đánh giá phim tồn tại
        MovieReview movieReview = movieReviewRepository.findById(requestDto.getReviewId().intValue())
                .orElseThrow(() -> new BusinessException("Đánh giá phim không tồn tại"));
        
        // Tạo ReviewImage mới
        ReviewImage reviewImage = new ReviewImage();
        reviewImage.setMovieReview(movieReview);
        reviewImage.setImageUrl(requestDto.getImageUrl());
        reviewImage.setDescription(requestDto.getDescription());
        
        ReviewImage savedImage = reviewImageRepository.save(reviewImage);
        return convertToResponseDto(savedImage);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReviewImageResponseDto> getReviewImages(Long reviewId) {
        List<ReviewImage> images = reviewImageRepository.findByMovieReviewIdOrderByCreatedAtDesc(reviewId);
        return images.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public ReviewImageResponseDto getReviewImageById(Long imageId) {
        ReviewImage reviewImage = reviewImageRepository.findById(imageId)
                .orElseThrow(() -> new BusinessException("Hình ảnh đánh giá không tồn tại"));
        return convertToResponseDto(reviewImage);
    }
    
    @Override
    public ReviewImageResponseDto updateReviewImage(Long imageId, ReviewImageRequestDto requestDto) {
        ReviewImage reviewImage = reviewImageRepository.findById(imageId)
                .orElseThrow(() -> new BusinessException("Hình ảnh đánh giá không tồn tại"));
        
        // Cập nhật thông tin
        reviewImage.setImageUrl(requestDto.getImageUrl());
        reviewImage.setDescription(requestDto.getDescription());
        
        ReviewImage updatedImage = reviewImageRepository.save(reviewImage);
        return convertToResponseDto(updatedImage);
    }
    
    @Override
    public void deleteReviewImage(Long imageId) {
        if (!reviewImageRepository.existsById(imageId)) {
            throw new BusinessException("Hình ảnh đánh giá không tồn tại");
        }
        reviewImageRepository.deleteById(imageId);
    }
    
    @Override
    public void deleteAllReviewImages(Long reviewId) {
        reviewImageRepository.deleteByMovieReviewId(reviewId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countReviewImages(Long reviewId) {
        return reviewImageRepository.countByMovieReviewId(reviewId);
    }
    
    private ReviewImageResponseDto convertToResponseDto(ReviewImage reviewImage) {
        return new ReviewImageResponseDto(
                reviewImage.getId(),
                reviewImage.getMovieReview().getIdDanhGia().longValue(),
                reviewImage.getImageUrl(),
                reviewImage.getDescription(),
                reviewImage.getCreatedAt()
        );
    }
}

