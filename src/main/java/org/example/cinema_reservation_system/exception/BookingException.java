package org.example.cinema_reservation_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when there are booking-related business logic errors
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class BookingException extends RuntimeException {
    
    public BookingException(String message) {
        super(message);
    }
    
    public BookingException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static BookingException seatAlreadyBooked(String seatInfo) {
        return new BookingException("Ghế đã được đặt: " + seatInfo);
    }
    
    public static BookingException showtimeNotAvailable(String showtimeInfo) {
        return new BookingException("Lịch chiếu không khả dụng: " + showtimeInfo);
    }
    
    public static BookingException customerNotEligible(String customerInfo) {
        return new BookingException("Khách hàng không đủ điều kiện đặt vé: " + customerInfo);
    }
    
    public static BookingException bookingExpired(String bookingInfo) {
        return new BookingException("Đặt vé đã hết hạn: " + bookingInfo);
    }
    
    public static BookingException paymentRequired(String bookingInfo) {
        return new BookingException("Yêu cầu thanh toán cho đặt vé: " + bookingInfo);
    }
}











































