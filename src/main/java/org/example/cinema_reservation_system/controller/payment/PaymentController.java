package org.example.cinema_reservation_system.controller.payment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.dto.payment.PaymentRequestDto;
import org.example.cinema_reservation_system.dto.payment.PaymentResponseDto;
import org.example.cinema_reservation_system.service.payment.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:5173")
public class PaymentController {
    private final PaymentService thanhToanService;

    @PostMapping
    public ResponseEntity<PaymentResponseDto> thanhToan(@Valid @RequestBody PaymentRequestDto request) {
        return ResponseEntity.ok(thanhToanService.thanhToanHoaDon(request));
    }
}
