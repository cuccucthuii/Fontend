package org.example.cinema_reservation_system.controller.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.dto.auth.SocialLoginRequestDto;
import org.example.cinema_reservation_system.dto.auth.SocialLoginResponseDto;
import org.example.cinema_reservation_system.service.utility.SocialLoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/social")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class SocialAuthController {

    private final SocialLoginService socialLoginService;

    // Test endpoint
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Social OAuth controller is working!");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    // Test POST endpoint để kiểm tra request body
    @PostMapping("/test-post")
    public ResponseEntity<Map<String, Object>> testPost(@RequestBody(required = false) Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "POST endpoint is working!");
        response.put("received_data", request);
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    // Test Google OAuth với mock response
    @PostMapping("/google-mock")
    public ResponseEntity<Map<String, Object>> loginWithGoogleMock(@RequestBody(required = false) Map<String, Object> requestMap) {
        try {
            System.out.println("Received Google OAuth mock request: " + requestMap);
            
            // Kiểm tra request body
            if (requestMap == null || requestMap.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Request body is required");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Kiểm tra accessToken
            String accessToken = (String) requestMap.get("accessToken");
            if (accessToken == null || accessToken.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Access token is required");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Mock response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Google OAuth mock successful");
            response.put("token", "mock_jwt_token_" + System.currentTimeMillis());
            response.put("userId", 1);
            response.put("username", requestMap.get("email") != null ? requestMap.get("email") : "test@gmail.com");
            response.put("email", requestMap.get("email") != null ? requestMap.get("email") : "test@gmail.com");
            response.put("role", "CLIENT");
            response.put("provider", "GOOGLE");
            response.put("isNewUser", true);
            response.put("received_data", requestMap);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error in Google OAuth mock: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Đăng nhập bằng Google
    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody(required = false) Map<String, Object> requestMap) {
        try {
            System.out.println("Received Google OAuth request: " + requestMap);
            
            // Kiểm tra request body
            if (requestMap == null || requestMap.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Request body is required");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Tạo SocialLoginRequestDto từ Map
            SocialLoginRequestDto request = new SocialLoginRequestDto();
            request.setAccessToken((String) requestMap.get("accessToken"));
            request.setProvider("GOOGLE");
            request.setEmail((String) requestMap.get("email"));
            request.setName((String) requestMap.get("name"));
            request.setPicture((String) requestMap.get("picture"));
            request.setSocialId((String) requestMap.get("socialId"));
            
            // Kiểm tra accessToken
            if (request.getAccessToken() == null || request.getAccessToken().trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Access token is required");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            System.out.println("Processed request: " + request);
            SocialLoginResponseDto response = socialLoginService.loginWithGoogle(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error in Google OAuth: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Đăng nhập chung (tự động detect provider)
    @PostMapping("/login")
    public ResponseEntity<SocialLoginResponseDto> socialLogin(@Valid @RequestBody SocialLoginRequestDto request) {
        SocialLoginResponseDto response = socialLoginService.processSocialLogin(request);
        return ResponseEntity.ok(response);
    }

    // Liên kết tài khoản hiện tại với social account
    @PostMapping("/link")
    public ResponseEntity<SocialLoginResponseDto> linkSocialAccount(@Valid @RequestBody SocialLoginRequestDto request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = getCurrentUserId(auth);
        
        SocialLoginResponseDto response = socialLoginService.linkSocialAccount(userId, request);
        return ResponseEntity.ok(response);
    }

    // Hủy liên kết social account
    @DeleteMapping("/unlink/{provider}")
    public ResponseEntity<String> unlinkSocialAccount(@PathVariable String provider) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = getCurrentUserId(auth);
        
        socialLoginService.unlinkSocialAccount(userId, provider);
        return ResponseEntity.ok("Đã hủy liên kết với " + provider);
    }

    // Lấy danh sách social account đã liên kết
    @GetMapping("/linked-accounts")
    public ResponseEntity<SocialLoginResponseDto> getLinkedAccounts() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = getCurrentUserId(auth);
        
        SocialLoginResponseDto response = socialLoginService.getLinkedAccounts(userId);
        return ResponseEntity.ok(response);
    }

    // Xác thực token social
    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateSocialToken(@RequestParam String token, @RequestParam String provider) {
        boolean isValid = socialLoginService.validateSocialToken(token, provider);
        return ResponseEntity.ok(isValid);
    }

    // Helper method để lấy current user ID
    private Integer getCurrentUserId(Authentication auth) {
        if (auth != null && auth.getPrincipal() instanceof org.example.cinema_reservation_system.entity.UserAccount) {
            return ((org.example.cinema_reservation_system.entity.UserAccount) auth.getPrincipal()).getIdTaiKhoan();
        }
        return null;
    }
}
