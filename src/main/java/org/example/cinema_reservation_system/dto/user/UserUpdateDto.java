package org.example.cinema_reservation_system.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class UserUpdateDto {
    @NotBlank(message = "Tên không được để trống")
    private String tenDangNhap;
    
    private String ten;
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String diaChi;

//    // Có thể cho phép đổi vai trò nếu là ADMIN
//    private Integer idVaiTro;

//    @NotBlank(message = "Mật khẩu không được để trống")
//    private String matKhau;
}

