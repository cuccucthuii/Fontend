package org.example.cinema_reservation_system.service.review;

import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.dto.reviewdto.ReviewReplyRequestDto;
import org.example.cinema_reservation_system.dto.reviewdto.ReviewReplyResponseDto;
import org.example.cinema_reservation_system.entity.Employee;
import org.example.cinema_reservation_system.entity.MovieReview;
import org.example.cinema_reservation_system.entity.ReviewReply;
import org.example.cinema_reservation_system.exception.BusinessException;
import org.example.cinema_reservation_system.repository.employee.EmployeeRepository;
import org.example.cinema_reservation_system.repository.movie.MovieReviewRepository;
import org.example.cinema_reservation_system.repository.review.ReviewReplyRepository;
import org.example.cinema_reservation_system.service.review.ReviewReplyService;
import org.example.cinema_reservation_system.utils.enums.TrangThai;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewReplyServiceImpl implements ReviewReplyService {
    
    private final ReviewReplyRepository reviewReplyRepository;
    private final MovieReviewRepository movieReviewRepository;
    private final EmployeeRepository employeeRepository;
    
    @Override
    public ReviewReplyResponseDto addReviewReply(ReviewReplyRequestDto requestDto) {
        // Kiểm tra đánh giá phim tồn tại
        MovieReview movieReview = movieReviewRepository.findById(requestDto.getReviewId().intValue())
                .orElseThrow(() -> new BusinessException("Đánh giá phim không tồn tại"));
        
        // Kiểm tra nhân viên tồn tại (nếu có)
        Employee employee = null;
        if (requestDto.getEmployeeId() != null) {
            employee = employeeRepository.findById(requestDto.getEmployeeId().intValue())
                    .orElseThrow(() -> new BusinessException("Nhân viên không tồn tại"));
        }
        
        // Tạo ReviewReply mới
        ReviewReply reviewReply = new ReviewReply();
        reviewReply.setMovieReview(movieReview);
        reviewReply.setEmployee(employee);
        reviewReply.setReplyContent(requestDto.getReplyContent());
        reviewReply.setStatus(TrangThai.HOAT_DONG);
        
        ReviewReply savedReply = reviewReplyRepository.save(reviewReply);
        return convertToResponseDto(savedReply);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReviewReplyResponseDto> getReviewReplies(Long reviewId) {
        List<ReviewReply> replies = reviewReplyRepository.findByMovieReviewIdOrderByRepliedAtDesc(reviewId);
        return replies.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public ReviewReplyResponseDto getReviewReplyById(Long replyId) {
        ReviewReply reviewReply = reviewReplyRepository.findById(replyId)
                .orElseThrow(() -> new BusinessException("Phản hồi đánh giá không tồn tại"));
        return convertToResponseDto(reviewReply);
    }
    
    @Override
    public ReviewReplyResponseDto updateReviewReply(Long replyId, ReviewReplyRequestDto requestDto) {
        ReviewReply reviewReply = reviewReplyRepository.findById(replyId)
                .orElseThrow(() -> new BusinessException("Phản hồi đánh giá không tồn tại"));
        
        // Cập nhật nội dung
        reviewReply.setReplyContent(requestDto.getReplyContent());
        
        ReviewReply updatedReply = reviewReplyRepository.save(reviewReply);
        return convertToResponseDto(updatedReply);
    }
    
    @Override
    public void deleteReviewReply(Long replyId) {
        if (!reviewReplyRepository.existsById(replyId)) {
            throw new BusinessException("Phản hồi đánh giá không tồn tại");
        }
        reviewReplyRepository.deleteById(replyId);
    }
    
    @Override
    public void deleteAllReviewReplies(Long reviewId) {
        reviewReplyRepository.deleteByMovieReviewId(reviewId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReviewReplyResponseDto> getReviewRepliesByStatus(TrangThai status) {
        List<ReviewReply> replies = reviewReplyRepository.findByStatusOrderByRepliedAtDesc(status);
        return replies.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReviewReplyResponseDto> getReviewRepliesByEmployee(Long employeeId) {
        List<ReviewReply> replies = reviewReplyRepository.findByEmployeeIdOrderByRepliedAtDesc(employeeId);
        return replies.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public ReviewReplyResponseDto updateReplyStatus(Long replyId, TrangThai status) {
        ReviewReply reviewReply = reviewReplyRepository.findById(replyId)
                .orElseThrow(() -> new BusinessException("Phản hồi đánh giá không tồn tại"));
        
        reviewReply.setStatus(status);
        ReviewReply updatedReply = reviewReplyRepository.save(reviewReply);
        return convertToResponseDto(updatedReply);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countReviewReplies(Long reviewId) {
        return reviewReplyRepository.countByMovieReviewId(reviewId);
    }
    
    private ReviewReplyResponseDto convertToResponseDto(ReviewReply reviewReply) {
        return new ReviewReplyResponseDto(
                reviewReply.getId(),
                reviewReply.getMovieReview().getIdDanhGia().longValue(),
                reviewReply.getEmployee() != null ? reviewReply.getEmployee().getIdNhanVien().longValue() : null,
                reviewReply.getEmployee() != null ? reviewReply.getEmployee().getTenNhanVien() : null,
                reviewReply.getReplyContent(),
                reviewReply.getRepliedAt(),
                reviewReply.getStatus()
        );
    }
}

