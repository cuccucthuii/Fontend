package org.example.cinema_reservation_system.controller.seat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cinema_reservation_system.dto.SeatSelectionDTO;
import org.example.cinema_reservation_system.common.ApiError;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.example.cinema_reservation_system.service.seat.SeatSelectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seat-selection")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class SeatSelectionController {

    private final SeatSelectionService seatSelectionService;
    private final MessageSource messageSource;

    @GetMapping("/availability/{showTimeId}")
    public ResponseEntity<SeatSelectionDTO.AvailabilityResponse> getAvailability(@PathVariable Long showTimeId) {
        return ResponseEntity.ok(seatSelectionService.getAvailability(showTimeId));
    }

    @PostMapping("/hold")
    public ResponseEntity<?> holdSeats(@RequestBody SeatSelectionDTO.HoldRequest request) {
        try {
            return ResponseEntity.ok(seatSelectionService.holdSeats(request));
        } catch (IllegalStateException ex) {
            String code = inferCodeFromMessage(ex.getMessage());
            return ResponseEntity.status(409).body(new ApiError(code, i18nMessageFor(code, ex.getMessage())));
        }
    }

    @PostMapping("/release")
    public ResponseEntity<Void> releaseSeats(@RequestBody SeatSelectionDTO.ReleaseRequest request) {
        seatSelectionService.releaseSeats(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmSeats(@RequestBody SeatSelectionDTO.ConfirmRequest request) {
        try {
            boolean ok = seatSelectionService.confirmSeats(request);
            if (!ok) {
                return ResponseEntity.status(409).body(new ApiError("CONFIRM_FAILED", i18nMessageFor("CONFIRM_FAILED", "Seats unavailable or invalid hold token")));
            }
            return ResponseEntity.ok(true);
        } catch (IllegalStateException ex) {
            String code = inferCodeFromMessage(ex.getMessage());
            return ResponseEntity.status(409).body(new ApiError(code, i18nMessageFor(code, ex.getMessage())));
        }
    }

    private String inferCodeFromMessage(String msg) {
        if (msg == null) return "SEAT_CONFLICT";
        String lower = msg.toLowerCase();
        if (lower.contains("phiên giữ") || lower.contains("hold")) return "HOLD_EXISTS";
        if (lower.contains("đặt") || lower.contains("giữ")) return "SEAT_CONFLICT";
        return "CONFLICT";
    }

    private String i18nMessageFor(String code, String fallbackEnglish) {
        String key = switch (code) {
            case "SEAT_CONFLICT" -> "error.seat.conflict";
            case "HOLD_EXISTS" -> "error.seat.conflict";
            case "CONFIRM_FAILED" -> "error.seat.unavailable";
            case "TOKEN_INVALID" -> "error.hold.token.invalid";
            default -> "error.seat.conflict";
        };
        var locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, null, fallbackEnglish != null ? fallbackEnglish : key, locale);
    }
}
