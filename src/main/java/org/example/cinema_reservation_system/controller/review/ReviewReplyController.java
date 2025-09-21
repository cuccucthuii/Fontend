package org.example.cinema_reservation_system.controller.review;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.dto.reviewdto.ReviewReplyRequestDto;
import org.example.cinema_reservation_system.dto.reviewdto.ReviewReplyResponseDto;
import org.example.cinema_reservation_system.service.review.ReviewReplyService;
import org.example.cinema_reservation_system.utils.enums.TrangThai;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/review-replies")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReviewReplyController {
    
    private final ReviewReplyService reviewReplyService;
    
    /**
     * Thêm phản hồi cho đánh giá phim
     */
    @PostMapping
    public ResponseEntity<ReviewReplyResponseDto> addReviewReply(@Valid @RequestBody ReviewReplyRequestDto requestDto) {
        ReviewReplyResponseDto response = reviewReplyService.addReviewReply(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Lấy tất cả phản hồi của một đánh giá phim
     */
    @GetMapping("/review/{reviewId}")
    public ResponseEntity<List<ReviewReplyResponseDto>> getReviewReplies(@PathVariable Long reviewId) {
        List<ReviewReplyResponseDto> replies = reviewReplyService.getReviewReplies(reviewId);
        return ResponseEntity.ok(replies);
    }
    
    /**
     * Lấy phản hồi theo ID
     */
    @GetMapping("/{replyId}")
    public ResponseEntity<ReviewReplyResponseDto> getReviewReplyById(@PathVariable Long replyId) {
        ReviewReplyResponseDto reply = reviewReplyService.getReviewReplyById(replyId);
        return ResponseEntity.ok(reply);
    }
    
    /**
     * Cập nhật phản hồi
     */
    @PutMapping("/{replyId}")
    public ResponseEntity<ReviewReplyResponseDto> updateReviewReply(
            @PathVariable Long replyId,
            @Valid @RequestBody ReviewReplyRequestDto requestDto) {
        ReviewReplyResponseDto response = reviewReplyService.updateReviewReply(replyId, requestDto);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Xóa phản hồi
     */
    @DeleteMapping("/{replyId}")
    public ResponseEntity<Void> deleteReviewReply(@PathVariable Long replyId) {
        reviewReplyService.deleteReviewReply(replyId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Xóa tất cả phản hồi của một đánh giá phim
     */
    @DeleteMapping("/review/{reviewId}")
    public ResponseEntity<Void> deleteAllReviewReplies(@PathVariable Long reviewId) {
        reviewReplyService.deleteAllReviewReplies(reviewId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Lấy phản hồi theo trạng thái
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ReviewReplyResponseDto>> getReviewRepliesByStatus(@PathVariable TrangThai status) {
        List<ReviewReplyResponseDto> replies = reviewReplyService.getReviewRepliesByStatus(status);
        return ResponseEntity.ok(replies);
    }
    
    /**
     * Lấy phản hồi của một nhân viên
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<ReviewReplyResponseDto>> getReviewRepliesByEmployee(@PathVariable Long employeeId) {
        List<ReviewReplyResponseDto> replies = reviewReplyService.getReviewRepliesByEmployee(employeeId);
        return ResponseEntity.ok(replies);
    }
    
    /**
     * Cập nhật trạng thái phản hồi
     */
    @PatchMapping("/{replyId}/status")
    public ResponseEntity<ReviewReplyResponseDto> updateReplyStatus(
            @PathVariable Long replyId,
            @RequestParam TrangThai status) {
        ReviewReplyResponseDto response = reviewReplyService.updateReplyStatus(replyId, status);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Đếm số phản hồi của một đánh giá phim
     */
    @GetMapping("/review/{reviewId}/count")
    public ResponseEntity<Long> countReviewReplies(@PathVariable Long reviewId) {
        long count = reviewReplyService.countReviewReplies(reviewId);
        return ResponseEntity.ok(count);
    }
}

