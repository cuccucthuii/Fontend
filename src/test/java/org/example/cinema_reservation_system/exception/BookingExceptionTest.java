package org.example.cinema_reservation_system.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.junit.jupiter.api.Assertions.*;

class BookingExceptionTest {

    @Test
    void testBookingExceptionConstructor() {
        // Given
        String message = "Test booking exception";
        
        // When
        BookingException exception = new BookingException(message);
        
        // Then
        assertEquals(message, exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getClass().getAnnotation(ResponseStatus.class).value());
    }

    @Test
    void testBookingExceptionWithCause() {
        // Given
        String message = "Test booking exception";
        Throwable cause = new RuntimeException("Root cause");
        
        // When
        BookingException exception = new BookingException(message, cause);
        
        // Then
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testSeatAlreadyBooked() {
        // When
        BookingException exception = BookingException.seatAlreadyBooked("A1");
        
        // Then
        assertEquals("Ghế đã được đặt: A1", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getClass().getAnnotation(ResponseStatus.class).value());
    }

    @Test
    void testShowtimeNotAvailable() {
        // When
        BookingException exception = BookingException.showtimeNotAvailable("2024-01-01 20:00");
        
        // Then
        assertEquals("Lịch chiếu không khả dụng: 2024-01-01 20:00", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getClass().getAnnotation(ResponseStatus.class).value());
    }

    @Test
    void testCustomerNotEligible() {
        // When
        BookingException exception = BookingException.customerNotEligible("VIP required");
        
        // Then
        assertEquals("Khách hàng không đủ điều kiện đặt vé: VIP required", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getClass().getAnnotation(ResponseStatus.class).value());
    }

    @Test
    void testBookingExpired() {
        // When
        BookingException exception = BookingException.bookingExpired("Booking #123");
        
        // Then
        assertEquals("Đặt vé đã hết hạn: Booking #123", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getClass().getAnnotation(ResponseStatus.class).value());
    }

    @Test
    void testPaymentRequired() {
        // When
        BookingException exception = BookingException.paymentRequired("Booking #456");
        
        // Then
        assertEquals("Yêu cầu thanh toán cho đặt vé: Booking #456", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getClass().getAnnotation(ResponseStatus.class).value());
    }
}











































