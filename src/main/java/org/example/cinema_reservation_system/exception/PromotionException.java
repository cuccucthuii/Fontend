package org.example.cinema_reservation_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when there are promotion-related business logic errors
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class PromotionException extends RuntimeException {
    
    public PromotionException(String message) {
        super(message);
    }
    
    public PromotionException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static PromotionException promotionExpired(String promotionInfo) {
        return new PromotionException("Khuyến mãi đã hết hạn: " + promotionInfo);
    }
    
    public static PromotionException promotionNotApplicable(String promotionInfo, String reason) {
        return new PromotionException("Khuyến mãi không thể áp dụng: " + promotionInfo + " - Lý do: " + reason);
    }
    
    public static PromotionException promotionLimitReached(String promotionInfo) {
        return new PromotionException("Đã đạt giới hạn sử dụng khuyến mãi: " + promotionInfo);
    }
    
    public static PromotionException customerNotEligible(String customerInfo, String promotionInfo) {
        return new PromotionException("Khách hàng không đủ điều kiện: " + customerInfo + " cho khuyến mãi: " + promotionInfo);
    }
    
    public static PromotionException invalidPromotionCode(String code) {
        return new PromotionException("Mã khuyến mãi không hợp lệ: " + code);
    }
    
    public static PromotionException promotionNotActive(String promotionInfo) {
        return new PromotionException("Khuyến mãi không hoạt động: " + promotionInfo);
    }
}











































