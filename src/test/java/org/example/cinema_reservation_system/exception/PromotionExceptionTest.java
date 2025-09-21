package org.example.cinema_reservation_system.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.junit.jupiter.api.Assertions.*;

class PromotionExceptionTest {

    @Test
    void testPromotionExceptionConstructor() {
        // Given
        String message = "Test promotion exception";
        
        // When
        PromotionException exception = new PromotionException(message);
        
        // Then
        assertEquals(message, exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getClass().getAnnotation(ResponseStatus.class).value());
    }

    @Test
    void testPromotionExceptionWithCause() {
        // Given
        String message = "Test promotion exception";
        Throwable cause = new RuntimeException("Root cause");
        
        // When
        PromotionException exception = new PromotionException(message, cause);
        
        // Then
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testPromotionExpired() {
        // When
        PromotionException exception = PromotionException.promotionExpired("Summer Sale 2024");
        
        // Then
        assertEquals("Khuyến mãi đã hết hạn: Summer Sale 2024", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getClass().getAnnotation(ResponseStatus.class).value());
    }

    @Test
    void testPromotionNotApplicable() {
        // When
        PromotionException exception = PromotionException.promotionNotApplicable("Student Discount", "Minimum purchase not met");
        
        // Then
        assertEquals("Khuyến mãi không thể áp dụng: Student Discount - Lý do: Minimum purchase not met", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getClass().getAnnotation(ResponseStatus.class).value());
    }

    @Test
    void testPromotionLimitReached() {
        // When
        PromotionException exception = PromotionException.promotionLimitReached("First Time User");
        
        // Then
        assertEquals("Đã đạt giới hạn sử dụng khuyến mãi: First Time User", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getClass().getAnnotation(ResponseStatus.class).value());
    }

    @Test
    void testCustomerNotEligible() {
        // When
        PromotionException exception = PromotionException.customerNotEligible("New customer", "Birthday Special");
        
        // Then
        assertEquals("Khách hàng không đủ điều kiện: New customer cho khuyến mãi: Birthday Special", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getClass().getAnnotation(ResponseStatus.class).value());
    }

    @Test
    void testInvalidPromotionCode() {
        // When
        PromotionException exception = PromotionException.invalidPromotionCode("INVALID123");
        
        // Then
        assertEquals("Mã khuyến mãi không hợp lệ: INVALID123", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getClass().getAnnotation(ResponseStatus.class).value());
    }

    @Test
    void testPromotionNotActive() {
        // When
        PromotionException exception = PromotionException.promotionNotActive("Weekend Special");
        
        // Then
        assertEquals("Khuyến mãi không hoạt động: Weekend Special", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getClass().getAnnotation(ResponseStatus.class).value());
    }
}











































