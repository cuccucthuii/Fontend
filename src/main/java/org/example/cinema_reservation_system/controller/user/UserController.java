package org.example.cinema_reservation_system.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cinema_reservation_system.dto.auth.ChangePasswordDto;
import org.example.cinema_reservation_system.dto.user.AdminUpdateUserDto;
import org.example.cinema_reservation_system.dto.userdto.UserResponseDto;
import org.example.cinema_reservation_system.dto.user.UserUpdateDto;
import org.example.cinema_reservation_system.dto.user.UpdateAvatarRequestDto;
import org.example.cinema_reservation_system.repository.useraccount.UserAccountRepository;
import org.example.cinema_reservation_system.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserService userService;

    @Autowired
    private UserAccountRepository userAccountRepository;

    // /api/user/all
    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }


    @PutMapping("/update-current-user")
    public ResponseEntity<UserResponseDto> updateCurrentUser(@Valid @RequestBody UserUpdateDto dto) {
        UserResponseDto response = userService.updateCurrentUser(dto);
        return ResponseEntity.ok(response);
    }

    // /api/user/change-password
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDto dto) {
        userService.changePassword(dto);
        return ResponseEntity.ok("Đổi mật khẩu thành công!");
    }

    @PostMapping("/api/auth/check-username")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        boolean exists = userAccountRepository.existsByTenDangNhap(username);
        return ResponseEntity.ok(Collections.singletonMap("exists", exists));
    }

    // /api/user/update-avatar
    @PutMapping("/update-avatar")
    public ResponseEntity<UserResponseDto> updateAvatar(@Valid @RequestBody UpdateAvatarRequestDto dto) {
        UserResponseDto response = userService.updateAvatar(dto);
        return ResponseEntity.ok(response);
    }

    //thêm mới
    // Khóa tài khoản
    @PutMapping("/{username}/lock")
    public ResponseEntity<?> lockUser(@PathVariable String username) {
        userService.lockUser(username);
        return ResponseEntity.ok("Đã khóa tài khoản " + username);
    }

    // Mở khóa tài khoản
    @PutMapping("/{username}/unlock")
    public ResponseEntity<?> unlockUser(@PathVariable String username) {
        userService.unlockUser(username);
        return ResponseEntity.ok("Đã mở khóa tài khoản " + username);
    }

    //theem mới
    // /api/user/{username}/reset-password (tự sinh mật khẩu)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{username}/reset-password")
    public ResponseEntity<?> adminResetPassword(@PathVariable String username) {
        userService.adminResetPassword(username);
        return ResponseEntity.ok("Đã reset mật khẩu cho tài khoản " + username + ". Vui lòng kiểm tra email đã đăng ký.");
    }

    // Admin update user theo username
    @PutMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')") // tùy chính sách
    public ResponseEntity<UserResponseDto> adminUpdateUser(
            @PathVariable String username,
            @Valid @RequestBody AdminUpdateUserDto dto) {
        UserResponseDto updated = userService.adminUpdateUser(username, dto);
        return ResponseEntity.ok(updated);
    }

    //lấy danh sách tài khoản đã xóa mềm
    @GetMapping("/trash")
    public ResponseEntity<List<UserResponseDto>> getTrashUsers() {
        return ResponseEntity.ok(userService.getTrashUsers());
    }

    //khôi phục user đã xóa mềm
    @PutMapping("/{username}/restore")
    public ResponseEntity<?> restoreUser(@PathVariable String username) {
        userService.restoreUser(username);
        return ResponseEntity.ok("Đã khôi phục tài khoản " + username);
    }

    //xóa mềm
    @DeleteMapping("/{username}")
    public ResponseEntity<?> softDelete(@PathVariable String username) {
        userService.softDeleteUser(username);
        return ResponseEntity.ok("Đã chuyển tài khoản " + username + " vào Thùng rác");
    }

    //xóa vĩnh viễn khỏi DB
    @DeleteMapping("/{username}/permanent")
    public ResponseEntity<?> hardDelete(@PathVariable String username) {
        userService.hardDeleteUser(username);
        return ResponseEntity.ok("Đã xóa vĩnh viễn tài khoản " + username);
    }
}

