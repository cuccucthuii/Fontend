package org.example.cinema_reservation_system.exception;

import org.example.cinema_reservation_system.common.ApiError;
import org.example.cinema_reservation_system.dto.user.ResponseDto;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatus(ResponseStatusException ex) {
        String code = switch (ex.getStatusCode()) {
            case HttpStatus.BAD_REQUEST -> "BAD_REQUEST";
            case HttpStatus.NOT_FOUND -> "NOT_FOUND";
            case HttpStatus.CONFLICT -> "CONFLICT";
            case HttpStatus.FORBIDDEN -> "FORBIDDEN";
            default -> "ERROR";
        };
        String key = toKey(code);
        String message = messageSource.getMessage(key, null, ex.getReason() != null ? ex.getReason() : key, LocaleContextHolder.getLocale());
        return ResponseEntity.status(ex.getStatusCode()).body(new ApiError(code, message));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage());
        
        // Return detailed validation errors for better debugging
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        
        // Also return ApiError for consistency
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .orElse("Validation error");
        String i18n = messageSource.getMessage("error.validation", null, message, LocaleContextHolder.getLocale());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError("VALIDATION_ERROR", i18n));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalState(IllegalStateException ex) {
        String i18n = messageSource.getMessage("error.conflict", null, ex.getMessage(), LocaleContextHolder.getLocale());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError("CONFLICT", i18n));
    }
    
    @ExceptionHandler(BookingException.class)
    public ResponseEntity<ApiError> handleBookingException(BookingException ex) {
        String i18n = messageSource.getMessage("error.booking.conflict", null, ex.getMessage(), LocaleContextHolder.getLocale());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError("BOOKING_CONFLICT", i18n));
    }
    
    @ExceptionHandler(PromotionException.class)
    public ResponseEntity<ApiError> handlePromotionException(PromotionException ex) {
        String i18n = messageSource.getMessage("error.promotion.conflict", null, ex.getMessage(), LocaleContextHolder.getLocale());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError("PROMOTION_CONFLICT", i18n));
    }

    // ========== LEGACY EXCEPTION HANDLERS (from GlobalControllerHandler) ==========
    
    @ExceptionHandler(UserNotfoundException.class)
    public ResponseEntity<ResponseDto<Void>> handleUserNotFoundException(UserNotfoundException ex) {
        log.error("User not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ResponseDto.fail(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(InputInvalidException.class)
    public ResponseEntity<ResponseDto<Void>> handleInvalidRequestException(InputInvalidException ex) {
        log.error("Invalid input: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseDto.fail(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(UserNameOrPasswordInvalidException.class)
    public ResponseEntity<ResponseDto<Void>> handleUserNameOrPasswordInvalidException(UserNameOrPasswordInvalidException ex) {
        log.error("Invalid credentials: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ResponseDto.fail(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleOthers(Exception ex) {
        log.error("Unexpected error occurred", ex);
        String i18n = messageSource.getMessage("error.internal", null, "Internal error", LocaleContextHolder.getLocale());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiError("ERROR", i18n));
    }

    // ========== STATIC RESOURCE NOT FOUND → 404, không trả 500 ==========
    @ExceptionHandler({NoResourceFoundException.class, NoHandlerFoundException.class})
    public ResponseEntity<ApiError> handleStaticResourceNotFound(Exception ex) {
        log.warn("Static resource not found: {}", ex.getMessage());
        String i18n = messageSource.getMessage("error.not_found", null, "Not found", LocaleContextHolder.getLocale());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError("NOT_FOUND", i18n));
    }

    private String toKey(String code) {
        return switch (code) {
            case "BAD_REQUEST" -> "error.bad_request";
            case "NOT_FOUND" -> "error.not_found";
            case "FORBIDDEN" -> "error.forbidden";
            case "CONFLICT" -> "error.conflict";
            default -> "error.internal";
        };
    }
}
