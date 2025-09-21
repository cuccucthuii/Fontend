package org.example.cinema_reservation_system.controller.seat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cinema_reservation_system.dto.seatdto.SeatLayoutDTO;
import org.example.cinema_reservation_system.service.seat.SeatLayoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seat-layout")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
public class SeatLayoutController {

    private final SeatLayoutService seatLayoutService;

    /**
     * Lấy layout ghế cho suất chiếu cụ thể
     */
    @GetMapping("/showtime/{showTimeId}")
    public ResponseEntity<SeatLayoutDTO> getSeatLayoutForShowtime(@PathVariable Long showTimeId) {
        try {
            SeatLayoutDTO layout = seatLayoutService.getSeatLayoutForShowtime(showTimeId);
            return ResponseEntity.ok(layout);
        } catch (Exception e) {
            log.error("Error getting seat layout for showtime: {}", showTimeId, e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Lấy layout ghế cho phòng chiếu
     */
    @GetMapping("/room/{roomId}")
    public ResponseEntity<SeatLayoutDTO> getSeatLayoutForRoom(@PathVariable Integer roomId) {
        try {
            SeatLayoutDTO layout = seatLayoutService.getSeatLayoutForRoom(roomId);
            return ResponseEntity.ok(layout);
        } catch (Exception e) {
            log.error("Error getting seat layout for room: {}", roomId, e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Lấy thông tin giá vé cho ghế cụ thể
     */
    @GetMapping("/seat/{seatId}/price")
    public ResponseEntity<SeatLayoutDTO.SeatPriceInfo> getSeatPrice(@PathVariable Integer seatId) {
        try {
            SeatLayoutDTO.SeatPriceInfo priceInfo = seatLayoutService.getSeatPrice(seatId);
            return ResponseEntity.ok(priceInfo);
        } catch (Exception e) {
            log.error("Error getting seat price for seat: {}", seatId, e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Tính tổng giá vé cho danh sách ghế
     */
    @PostMapping("/calculate-total")
    public ResponseEntity<SeatLayoutDTO.TotalPriceResponse> calculateTotalPrice(
            @RequestBody SeatLayoutDTO.TotalPriceRequest request) {
        try {
            SeatLayoutDTO.TotalPriceResponse totalPrice = seatLayoutService.calculateTotalPrice(request);
            return ResponseEntity.ok(totalPrice);
        } catch (Exception e) {
            log.error("Error calculating total price", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Lấy danh sách ghế theo trạng thái
     */
    @GetMapping("/showtime/{showTimeId}/seats/status/{status}")
    public ResponseEntity<List<SeatLayoutDTO.SeatInfo>> getSeatsByStatus(
            @PathVariable Long showTimeId, 
            @PathVariable String status) {
        try {
            List<SeatLayoutDTO.SeatInfo> seats = seatLayoutService.getSeatsByStatus(showTimeId, status);
            return ResponseEntity.ok(seats);
        } catch (Exception e) {
            log.error("Error getting seats by status: {} for showtime: {}", status, showTimeId, e);
            return ResponseEntity.badRequest().build();
        }
    }
}
