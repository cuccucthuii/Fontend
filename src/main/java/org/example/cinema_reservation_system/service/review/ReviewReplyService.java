package org.example.cinema_reservation_system.service.review;

import org.example.cinema_reservation_system.dto.reviewdto.ReviewReplyRequestDto;
import org.example.cinema_reservation_system.dto.reviewdto.ReviewReplyResponseDto;
import org.example.cinema_reservation_system.utils.enums.TrangThai;

import java.util.List;

public interface ReviewReplyService {
    
    /**
     * Thêm phản hồi cho đánh giá phim
     */
    ReviewReplyResponseDto addReviewReply(ReviewReplyRequestDto requestDto);
    
    /**
     * Lấy tất cả phản hồi của một đánh giá phim
     */
    List<ReviewReplyResponseDto> getReviewReplies(Long reviewId);
    
    /**
     * Lấy phản hồi theo ID
     */
    ReviewReplyResponseDto getReviewReplyById(Long replyId);
    
    /**
     * Cập nhật phản hồi
     */
    ReviewReplyResponseDto updateReviewReply(Long replyId, ReviewReplyRequestDto requestDto);
    
    /**
     * Xóa phản hồi
     */
    void deleteReviewReply(Long replyId);
    
    /**
     * Xóa tất cả phản hồi của một đánh giá phim
     */
    void deleteAllReviewReplies(Long reviewId);
    
    /**
     * Lấy phản hồi theo trạng thái
     */
    List<ReviewReplyResponseDto> getReviewRepliesByStatus(TrangThai status);
    
    /**
     * Lấy phản hồi của một nhân viên
     */
    List<ReviewReplyResponseDto> getReviewRepliesByEmployee(Long employeeId);
    
    /**
     * Cập nhật trạng thái phản hồi
     */
    ReviewReplyResponseDto updateReplyStatus(Long replyId, TrangThai status);
    
    /**
     * Đếm số phản hồi của một đánh giá phim
     */
    long countReviewReplies(Long reviewId);
}

