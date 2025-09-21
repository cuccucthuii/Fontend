package org.example.cinema_reservation_system.controller.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.dto.auth.LoginDto;
import org.example.cinema_reservation_system.dto.auth.PasswordResetRequestDto;
import org.example.cinema_reservation_system.dto.auth.PasswordResetDto;
import org.example.cinema_reservation_system.dto.auth.ChangePasswordDto;
import org.example.cinema_reservation_system.dto.user.CustomerRegisterDto;
import org.example.cinema_reservation_system.dto.user.EmployeeRegisterDto;
import org.example.cinema_reservation_system.entity.UserAccount;
import org.example.cinema_reservation_system.repository.useraccount.UserAccountRepository;
import org.example.cinema_reservation_system.security.JwtService;
import org.example.cinema_reservation_system.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/register/customer")
    public ResponseEntity<?> registerCustomer(@RequestBody @Valid CustomerRegisterDto dto) {
        userService.registerCustomer(dto);
        return ResponseEntity.ok("Đăng ký khách hàng thành công");
    }

    @PostMapping("/register-employee")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> registerEmployee(@Valid @RequestBody EmployeeRegisterDto employeeRegisterDto) {
        return ResponseEntity.ok(userService.registerEmployee(employeeRegisterDto));
    }

    @PostMapping("/register-employee-self")
    public ResponseEntity<String> registerEmployeeSelf(@Valid @RequestBody EmployeeRegisterDto employeeRegisterDto) {
        // Không cần @PreAuthorize - nhân viên có thể tự đăng ký
        return ResponseEntity.ok(userService.registerEmployeeSelf(employeeRegisterDto));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto dto) {
        try {
            UserAccount user = userService.login(dto);

            // Tạo access token và refresh token
            String accessToken = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            // Lưu refresh token vào database
            user.setRefreshToken(refreshToken);
            user.setRefreshTokenExpiresAt(LocalDateTime.now().plusDays(7));
            userAccountRepository.save(user);

            log.info("User {} logged in successfully", user.getTenDangNhap());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Đăng nhập thành công");
            response.put("role", user.getVaiTro().getTenVaiTro());
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("username", user.getTenDangNhap());
            response.put("userId", user.getIdTaiKhoan());
            response.put("avatarUrl", user.getAvatarUrl());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Login failed for user: {}", dto.getTenDangNhap(), e);
            return ResponseEntity.badRequest().body("Đăng nhập thất bại: " + e.getMessage());
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody PasswordResetRequestDto dto) {
        userService.requestPasswordReset(dto.getEmail());
        return ResponseEntity.ok("Email đặt lại mật khẩu đã được gửi");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetDto dto) {
        userService.resetPassword(dto.getToken(), dto.getNewPassword());
        return ResponseEntity.ok("Mật khẩu đã được đặt lại thành công");
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto dto) {
        // Lấy user hiện tại từ security context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserAccount currentUser = (UserAccount) auth.getPrincipal();

        userService.changePassword(currentUser.getIdTaiKhoan(), dto.getCurrentPassword(), dto.getNewPassword());
        return ResponseEntity.ok("Mật khẩu đã được thay đổi thành công");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().body("Refresh token không được để trống");
        }

        try {
            // Validate refresh token format và signature
            if (!jwtService.validateToken(refreshToken)) {
                return ResponseEntity.badRequest().body("Refresh token không hợp lệ hoặc đã hết hạn");
            }

            // Kiểm tra đây có phải refresh token không
            if (!jwtService.isRefreshToken(refreshToken)) {
                return ResponseEntity.badRequest().body("Token không phải là refresh token");
            }

            // Lấy username từ refresh token
            String username = jwtService.extractUsername(refreshToken);
            UserAccount user = userAccountRepository.findByTenDangNhap(username).orElse(null);

            if (user == null) {
                return ResponseEntity.badRequest().body("Người dùng không tồn tại");
            }

            // Kiểm tra refresh token có khớp với database không
            if (!refreshToken.equals(user.getRefreshToken())) {
                return ResponseEntity.badRequest().body("Refresh token không khớp với database");
            }

            // Tạo access token mới
            String newAccessToken = jwtService.generateToken(user);

            // Token rotation: Tạo refresh token mới
            String newRefreshToken = jwtService.generateRefreshToken(user);

            // Cập nhật refresh token mới vào database
            user.setRefreshToken(newRefreshToken);
            user.setRefreshTokenExpiresAt(LocalDateTime.now().plusDays(7));
            userAccountRepository.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Refresh token thành công");
            response.put("accessToken", newAccessToken);
            response.put("refreshToken", newRefreshToken); // Trả về refresh token mới

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Refresh token failed: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Lỗi khi refresh token: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody(required = false) Map<String, String> request) {
        // Xóa refresh token khỏi database nếu có
        if (request != null && request.containsKey("refreshToken")) {
            String refreshToken = request.get("refreshToken");
            try {
                String username = jwtService.extractUsername(refreshToken);
                UserAccount user = userAccountRepository.findByTenDangNhap(username).orElse(null);
                if (user != null) {
                    user.setRefreshToken(null);
                    user.setRefreshTokenExpiresAt(null);
                    userAccountRepository.save(user);
                }
            } catch (Exception e) {
                log.error("Error during logout token cleanup: {}", e.getMessage(), e);
            }
        }

        return ResponseEntity.ok("Đăng xuất thành công");
    }
}