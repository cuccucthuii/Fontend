package org.example.cinema_reservation_system.controller.utility;

import lombok.extern.slf4j.Slf4j;
import org.example.cinema_reservation_system.service.utility.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class OTPController {

    @Autowired
    private OtpService otpService;

    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, Object>> sendOTP(@RequestBody Map<String, String> request) {
        try {
            String phone = request.get("phone");

            if (phone == null || phone.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Số điện thoại không được để trống"));
            }
            if (!phone.matches("^0[0-9]{9}$")) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Số điện thoại không hợp lệ"));
            }
            String email = request.get("email");
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Email không được để trống"));
            }
            String otp = otpService.generateAndSendOTP(phone, email);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "OTP đã được gửi thành công");

            Map<String, Object> data = new HashMap<>();
            data.put("phone", phone);
            data.put("expiresIn", otpService.getOTPExpiration());
            response.put("data", data);

            log.info("OTP sent successfully for phone: {}", phone);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error sending OTP", e);
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Không thể gửi OTP. Vui lòng thử lại sau."));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOTP(@RequestBody Map<String, String> request) {
        try {
            String phone = request.get("phone");
            String otp = request.get("otp");

            if (phone == null || phone.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Số điện thoại không được để trống"));
            }

            if (otp == null || otp.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Mã OTP không được để trống"));
            }

            // Verify OTP
            boolean isValid = otpService.verifyOTP(phone, otp);

            if (isValid) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "OTP hợp lệ");

                Map<String, Object> data = new HashMap<>();
                data.put("phone", phone);
                data.put("verified", true);
                response.put("data", data);

                log.info("OTP verified successfully for phone: {}", phone);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Mã OTP không đúng hoặc đã hết hạn"));
            }

        } catch (Exception e) {
            log.error("Error verifying OTP", e);
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Không thể xác thực OTP. Vui lòng thử lại sau."));
        }
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
}
