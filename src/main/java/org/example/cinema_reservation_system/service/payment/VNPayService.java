package org.example.cinema_reservation_system.service.payment;

import org.example.cinema_reservation_system.dto.payment.VNPayCreateRequest;
import org.example.cinema_reservation_system.dto.payment.VNPayCreateResponse;
import org.example.cinema_reservation_system.dto.payment.VNPayReturnResponse;
import org.example.cinema_reservation_system.dto.payment.VNPayIPNResponse;

import java.util.Map;

public interface VNPayService {
    
    /**
     * Create VNPay payment URL
     */
    VNPayCreateResponse createPaymentUrl(VNPayCreateRequest request);
    
    /**
     * Process VNPay return URL (user redirected back)
     */
    VNPayReturnResponse processReturn(Map<String, String> params);
    
    /**
     * Process VNPay IPN (Instant Payment Notification)
     */
    VNPayIPNResponse processIPN(Map<String, String> params);
    
    /**
     * Verify VNPay signature
     */
    boolean verifySignature(Map<String, String> params, String signature);
    
    /**
     * Generate VNPay signature
     */
    String generateSignature(Map<String, String> params);
}