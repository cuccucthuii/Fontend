package org.example.cinema_reservation_system.service.payment;

import org.example.cinema_reservation_system.config.VNPayConfig;
import org.example.cinema_reservation_system.dto.payment.VNPayCreateRequest;
import org.example.cinema_reservation_system.dto.payment.VNPayCreateResponse;
import org.example.cinema_reservation_system.dto.payment.VNPayReturnResponse;
import org.example.cinema_reservation_system.dto.payment.VNPayIPNResponse;
// import org.example.cinema_reservation_system.entity.Booking; // deprecated
// import org.example.cinema_reservation_system.utils.enums.TrangThaiDatVe;
// import org.example.cinema_reservation_system.utils.enums.PhuongThuc;
// import org.example.cinema_reservation_system.repository.booking.BookingRepository; // deprecated
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VNPayServiceImpl implements VNPayService {
    
    @Autowired
    private VNPayConfig vnPayConfig;
    
    // private BookingRepository bookingRepository; // removed in new flow
    
    private static final String HMAC_SHA512 = "HmacSHA512";
    
    @Override
    public VNPayCreateResponse createPaymentUrl(VNPayCreateRequest request) {
        try {
            // New flow: xác thực số tiền/đơn hàng ở tầng Invoice thay vì Booking (đã bỏ)
            
            // Generate transaction reference
            String vnpTxnRef = "REF_" + (request.getBookingId() != null ? request.getBookingId() : "INV") + "_" + System.currentTimeMillis();
            
            // Build VNPay parameters
            Map<String, String> vnpParams = new HashMap<>();
            vnpParams.put("vnp_Version", vnPayConfig.getVersion());
            vnpParams.put("vnp_Command", vnPayConfig.getCommand());
            vnpParams.put("vnp_TmnCode", vnPayConfig.getTmnCode());
            vnpParams.put("vnp_Amount", String.valueOf(request.getAmount() * 100)); // VNPay requires amount * 100
            vnpParams.put("vnp_CurrCode", vnPayConfig.getCurrCode());
            vnpParams.put("vnp_TxnRef", vnpTxnRef);
            
            // Use environment-specific URLs
            vnpParams.put("vnp_ReturnUrl", vnPayConfig.getReturnUrlForEnvironment());
            vnpParams.put("vnp_IpAddr", getClientIP()); // Get client IP for security
            vnpParams.put("vnp_OrderInfo", request.getOrderInfo());
            vnpParams.put("vnp_OrderType", request.getOrderType());
            vnpParams.put("vnp_Locale", vnPayConfig.getLocale());
            vnpParams.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
            
            if (request.getBankCode() != null && !request.getBankCode().isEmpty()) {
                vnpParams.put("vnp_BankCode", request.getBankCode());
            }
            
            // Generate signature using the same approach as the reference
            String vnpSecureHash = hmacSHA512(vnPayConfig.getHashSecret(), buildHashData(vnpParams));
            vnpParams.put("vnp_SecureHash", vnpSecureHash);
            
            // Build payment URL
            StringBuilder queryString = new StringBuilder();
            for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
                if (queryString.length() > 0) {
                    queryString.append("&");
                }
                queryString.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()));
                queryString.append("=");
                queryString.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
            }
            
            String paymentUrl = vnPayConfig.getPayUrlForEnvironment() + "?" + queryString.toString();
            
            return new VNPayCreateResponse("00", "Tạo URL thanh toán thành công", 
                paymentUrl, request.getBookingId(), request.getAmount(), request.getOrderInfo());
                
        } catch (Exception e) {
            return new VNPayCreateResponse("ERROR", "Lỗi tạo URL thanh toán: " + e.getMessage());
        }
    }
    
    /**
     * Get client IP address for security validation
     */
    private String getClientIP() {
        // In production, this should be injected via HttpServletRequest
        // For now, return a default IP for development
        return "127.0.0.1";
    }
    
    @Override
    public VNPayReturnResponse processReturn(Map<String, String> params) {
        try {
            // Verify signature
            if (!verifySignature(params, params.get("vnp_SecureHash"))) {
                return new VNPayReturnResponse("INVALID_SIGNATURE", "Chữ ký không hợp lệ");
            }
            
            String vnpResponseCode = params.get("vnp_ResponseCode");
            String vnpTxnRef = params.get("vnp_TxnRef");
            String vnpTransactionNo = params.get("vnp_TransactionNo");
            String vnpAmount = params.get("vnp_Amount");
            
            // Extract reference from transaction reference
            if (!vnpTxnRef.startsWith("REF_")) {
                return new VNPayReturnResponse("INVALID_TXN_REF", "Mã giao dịch không hợp lệ");
            }
            
            String[] parts = vnpTxnRef.split("_");
            if (parts.length < 2) {
                return new VNPayReturnResponse("INVALID_TXN_REF", "Mã giao dịch không hợp lệ");
            }
            
            // New flow: xác nhận theo REF, số tiền sẽ được hệ thống đối chiếu ở Invoice layer
            
            String paymentStatus;
            String redirectUrl;
            
            if ("00".equals(vnpResponseCode)) {
                // Payment successful
                paymentStatus = "SUCCESS";
                redirectUrl = "/payment/success?ref=" + vnpTxnRef;
                
                // TODO: cập nhật Invoice trạng thái thanh toán theo ref
            } else {
                // Payment failed
                paymentStatus = "FAILED";
                redirectUrl = "/payment/failed?ref=" + vnpTxnRef + "&code=" + vnpResponseCode;
            }
            
            long amount = Long.parseLong(vnpAmount) / 100;
            return new VNPayReturnResponse("00", "Xử lý thành công", 
                null, vnpTransactionNo, amount, paymentStatus, redirectUrl);
                
        } catch (Exception e) {
            return new VNPayReturnResponse("ERROR", "Lỗi xử lý: " + e.getMessage());
        }
    }
    
    @Override
    public VNPayIPNResponse processIPN(Map<String, String> params) {
        try {
            // Verify signature
            if (!verifySignature(params, params.get("vnp_SecureHash"))) {
                return new VNPayIPNResponse("97", "Checksum failed");
            }
            
            String vnpResponseCode = params.get("vnp_ResponseCode");
            String vnpTxnRef = params.get("vnp_TxnRef");
            String vnpTransactionNo = params.get("vnp_TransactionNo");
            String vnpAmount = params.get("vnp_Amount");

            // Validate reference format (new flow uses REF_...)
            if (!vnpTxnRef.startsWith("REF_")) {
                return new VNPayIPNResponse("99", "Invalid transaction reference");
            }

            // In new flow, amount and order validation is handled at Invoice layer

            // Verify TmnCode
            if (!vnPayConfig.getTmnCode().equals(params.get("vnp_TmnCode"))) {
                return new VNPayIPNResponse("02", "Invalid TmnCode");
            }
            
            if ("00".equals(vnpResponseCode)) {
                // Payment successful - confirm
                return new VNPayIPNResponse("00", "Confirm Success");
            } else {
                return new VNPayIPNResponse("00", "Order already confirmed");
            }
            
        } catch (Exception e) {
            return new VNPayIPNResponse("99", "Unknown error: " + e.getMessage());
        }
    }
    
    @Override
    public boolean verifySignature(Map<String, String> params, String signature) {
        try {
            String calculatedSignature = generateSignature(params);
            return calculatedSignature.equals(signature);
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public String generateSignature(Map<String, String> params) {
        // Remove vnp_SecureHash from params
        Map<String, String> filteredParams = new HashMap<>(params);
        filteredParams.remove("vnp_SecureHash");
        
        return hmacSHA512(vnPayConfig.getHashSecret(), buildHashData(filteredParams));
    }
    
    /**
     * Build hash data string from parameters (sorted by key)
     */
    private String buildHashData(Map<String, String> params) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        
        StringBuilder hashData = new StringBuilder();
        for (String key : fieldNames) {
            if (hashData.length() > 0) {
                hashData.append("&");
            }
            hashData.append(key).append("=").append(params.get(key));
        }
        
        return hashData.toString();
    }
    
    /**
     * Generate HMAC SHA512 signature
     */
    private String hmacSHA512(String key, String data) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA512);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_SHA512);
            mac.init(secretKeySpec);
            
            byte[] hashBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            
            // Convert to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
            
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Error generating HMAC SHA512 signature", e);
        }
    }
}
