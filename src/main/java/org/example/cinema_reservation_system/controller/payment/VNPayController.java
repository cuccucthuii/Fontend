package org.example.cinema_reservation_system.controller.payment;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.service.payment.VNPayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vnpay")
public class VNPayController {
    private final VNPayService vnPayService;

    @GetMapping("/create")
    public ResponseEntity<?> createUrl(@RequestParam Integer idHoaDon, HttpServletRequest request) throws UnsupportedEncodingException {
        String ip = request.getRemoteAddr();
        String url = vnPayService.createPaymentUrl(idHoaDon, ip);
        return ResponseEntity.ok(url);
    }
}
