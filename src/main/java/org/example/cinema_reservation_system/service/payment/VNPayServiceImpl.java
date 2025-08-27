package org.example.cinema_reservation_system.service.payment;

import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.config.VNPayConfig;
import org.example.cinema_reservation_system.entity.Invoice;
import org.example.cinema_reservation_system.repository.invoice.InvoiceRepository;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VNPayServiceImpl implements VNPayService {

    private final VNPayConfig vnPayConfig;
    private final InvoiceRepository invoiceRepository;

    @Override
    public String createPaymentUrl(Integer idHoaDon, String ip) throws UnsupportedEncodingException {
        Invoice hoaDon = invoiceRepository.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        // Nếu IP là localhost IPv6 thì đổi về IPv4
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }

        // Tạo mã giao dịch (txnRef) duy nhất
        String orderId = System.currentTimeMillis() + "_" + idHoaDon;
        hoaDon.setMaGiaoDichVNPay(orderId);
        invoiceRepository.save(hoaDon);

        // Các thông tin cơ bản
        String orderInfo = "Thanh toan ve phim";
        String vnpCreateDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        // ✅ Tham số gửi sang VNPay
        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", vnPayConfig.getTmnCode());
        vnpParams.put("vnp_Amount", hoaDon.getTongTien().multiply(BigDecimal.valueOf(100)).toBigInteger().toString());
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", orderId);
        vnpParams.put("vnp_OrderInfo", orderInfo);
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnURL", vnPayConfig.getReturnUrl()); // ✅ CHÍNH XÁC key này
        vnpParams.put("vnp_IpAddr", ip);
        vnpParams.put("vnp_CreateDate", vnpCreateDate);

        // ✅ Tạo chuỗi hash và query
        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (String field : fieldNames) {
            String value = vnpParams.get(field);
            hashData.append(field).append('=').append(value).append('&');
            query.append(field).append('=').append(URLEncoder.encode(value, StandardCharsets.US_ASCII)).append('&');
        }

        // Xoá dấu & cuối
        hashData.setLength(hashData.length() - 1);
        query.setLength(query.length() - 1);

        // ✅ Tạo chữ ký SHA512
        String secureHash = hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
        query.append("&vnp_SecureHash=").append(secureHash);

        // ✅ Log ra để kiểm tra
        System.out.println("🔐 [VNPay] Hash Data:     " + hashData);
        System.out.println("🔑 [VNPay] Secure Hash:   " + secureHash);
        System.out.println("🌐 [VNPay] Payment URL:   " + vnPayConfig.getPayUrl() + "?" + query);

        return vnPayConfig.getPayUrl() + "?" + query;
    }

    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] bytes = hmac512.doFinal(data.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Lỗi khi tạo hash", ex);
        }
    }
}
