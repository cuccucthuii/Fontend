package org.example.cinema_reservation_system.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminResetPasswordDto {

    // Username/Tên đăng nhập của tài khoản cần reset
    @NotBlank(message = "Tên đăng nhập không được để trống")
    private String tenDangNhap;

}