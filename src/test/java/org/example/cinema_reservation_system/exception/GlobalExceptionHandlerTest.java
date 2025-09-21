package org.example.cinema_reservation_system.exception;

import org.example.cinema_reservation_system.common.ApiError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private MessageSource messageSource;

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler(messageSource);
        LocaleContextHolder.setLocale(Locale.ENGLISH);
    }

    @Test
    void handleResponseStatus_NotFound_ReturnsCorrectResponse() {
        // Given
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        when(messageSource.getMessage(eq("error.not_found"), isNull(), eq("Resource not found"), any(Locale.class)))
                .thenReturn("Resource not found");

        // When
        ResponseEntity<ApiError> response = exceptionHandler.handleResponseStatus(ex);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("NOT_FOUND", response.getBody().getCode());
        assertEquals("Resource not found", response.getBody().getMessage());
    }

    @Test
    void handleResponseStatus_BadRequest_ReturnsCorrectResponse() {
        // Given
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input");
        when(messageSource.getMessage(eq("error.bad_request"), isNull(), eq("Invalid input"), any(Locale.class)))
                .thenReturn("Invalid input");

        // When
        ResponseEntity<ApiError> response = exceptionHandler.handleResponseStatus(ex);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("BAD_REQUEST", response.getBody().getCode());
        assertEquals("Invalid input", response.getBody().getMessage());
    }

    @Test
    void handleResponseStatus_Conflict_ReturnsCorrectResponse() {
        // Given
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.CONFLICT, "Resource conflict");
        when(messageSource.getMessage(eq("error.conflict"), isNull(), eq("Resource conflict"), any(Locale.class)))
                .thenReturn("Resource conflict");

        // When
        ResponseEntity<ApiError> response = exceptionHandler.handleResponseStatus(ex);

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("CONFLICT", response.getBody().getCode());
        assertEquals("Resource conflict", response.getBody().getMessage());
    }

    @Test
    void handleBookingException_ReturnsCorrectResponse() {
        // Given
        BookingException ex = new BookingException("Seat already booked");
        when(messageSource.getMessage(eq("error.booking.conflict"), isNull(), eq("Seat already booked"), any(Locale.class)))
                .thenReturn("Seat already booked");

        // When
        ResponseEntity<ApiError> response = exceptionHandler.handleBookingException(ex);

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("BOOKING_CONFLICT", response.getBody().getCode());
        assertEquals("Seat already booked", response.getBody().getMessage());
    }

    @Test
    void handlePromotionException_ReturnsCorrectResponse() {
        // Given
        PromotionException ex = new PromotionException("Promotion expired");
        when(messageSource.getMessage(eq("error.promotion.conflict"), isNull(), eq("Promotion expired"), any(Locale.class)))
                .thenReturn("Promotion expired");

        // When
        ResponseEntity<ApiError> response = exceptionHandler.handlePromotionException(ex);

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("PROMOTION_CONFLICT", response.getBody().getCode());
        assertEquals("Promotion expired", response.getBody().getMessage());
    }

    @Test
    void handleIllegalState_ReturnsCorrectResponse() {
        // Given
        IllegalStateException ex = new IllegalStateException("Invalid state");
        when(messageSource.getMessage(eq("error.conflict"), isNull(), eq("Invalid state"), any(Locale.class)))
                .thenReturn("Invalid state");

        // When
        ResponseEntity<ApiError> response = exceptionHandler.handleIllegalState(ex);

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("CONFLICT", response.getBody().getCode());
        assertEquals("Invalid state", response.getBody().getMessage());
    }

    @Test
    void handleOthers_ReturnsCorrectResponse() {
        // Given
        Exception ex = new Exception("Unknown error");
        when(messageSource.getMessage(eq("error.internal"), isNull(), eq("Internal error"), any(Locale.class)))
                .thenReturn("Internal error");

        // When
        ResponseEntity<ApiError> response = exceptionHandler.handleOthers(ex);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ERROR", response.getBody().getCode());
        assertEquals("Internal error", response.getBody().getMessage());
    }
}











































