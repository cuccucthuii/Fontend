package org.example.cinema_reservation_system.controller;

import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.entity.SeatHold;
import org.example.cinema_reservation_system.service.SeatHoldService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seat-holds")
@RequiredArgsConstructor
public class SeatHoldController {

    private final SeatHoldService seatHoldService;

    @PostMapping
    public ResponseEntity<SeatHold> hold(@RequestParam Integer suatChieuId,
                                         @RequestParam Integer gheNgoiId,
                                         @RequestParam(required = false) Integer userAccountId,
                                         @RequestParam String token,
                                         @RequestParam(defaultValue = "15") int minutes) {
        return ResponseEntity.ok(seatHoldService.holdSeat(suatChieuId, gheNgoiId, userAccountId, token, minutes));
    }

    @GetMapping("/active")
    public ResponseEntity<List<SeatHold>> active() {
        return ResponseEntity.ok(seatHoldService.listActiveHolds());
    }

    @PostMapping("/release/by-token")
    public ResponseEntity<Boolean> releaseByToken(@RequestParam String token) {
        return ResponseEntity.ok(seatHoldService.releaseByToken(token));
    }

    @PostMapping("/release/by-seat")
    public ResponseEntity<Boolean> releaseBySeat(@RequestParam Integer suatChieuId,
                                                 @RequestParam Integer gheNgoiId) {
        return ResponseEntity.ok(seatHoldService.releaseByShowAndSeat(suatChieuId, gheNgoiId));
    }
}


