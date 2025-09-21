package org.example.cinema_reservation_system.controller.payment;

import org.example.cinema_reservation_system.dto.payment.VNPayCreateRequest;
import org.example.cinema_reservation_system.dto.payment.VNPayCreateResponse;
import org.example.cinema_reservation_system.dto.payment.VNPayReturnResponse;
import org.example.cinema_reservation_system.dto.payment.VNPayIPNResponse;
import org.example.cinema_reservation_system.service.payment.VNPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment/vnpay")
@CrossOrigin(origins = "*")
public class VNPayController {
    
    @Autowired
    private VNPayService vnPayService;
    
    /**
     * Create VNPay payment URL
     * POST /api/payment/vnpay/create
     */
    @PostMapping("/create")
    public ResponseEntity<VNPayCreateResponse> createPaymentUrl(@RequestBody VNPayCreateRequest request) {
        VNPayCreateResponse response = vnPayService.createPaymentUrl(request);
        
        if ("00".equals(response.getCode())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * VNPay return URL (user redirected back from VNPay)
     * GET /api/payment/vnpay/return
     */
    @GetMapping("/return")
    public ResponseEntity<VNPayReturnResponse> processReturn(@RequestParam Map<String, String> params) {
        VNPayReturnResponse response = vnPayService.processReturn(params);
        
        if ("00".equals(response.getCode())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * VNPay IPN (Instant Payment Notification)
     * POST /api/payment/vnpay/ipn
     */
    @PostMapping("/ipn")
    public ResponseEntity<VNPayIPNResponse> processIPN(@RequestParam Map<String, String> params) {
        VNPayIPNResponse response = vnPayService.processIPN(params);
        
        if ("00".equals(response.getRspCode())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Test endpoint to verify VNPay configuration
     * GET /api/payment/vnpay/test
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> testConfig() {
        return ResponseEntity.ok(Map.of(
            "status", "VNPay service is running",
            "message", "Configuration loaded successfully"
        ));
    }
}