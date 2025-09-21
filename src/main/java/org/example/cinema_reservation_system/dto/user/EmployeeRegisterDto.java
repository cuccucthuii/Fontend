package org.example.cinema_reservation_system.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EmployeeRegisterDto {
    @NotBlank(message = "Tên đăng nhập không được để trống")
    private String tenDangNhap;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String matKhau;

    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "Tên nhân viên không được để trống")
    private String tenNhanVien;

    private String soDienThoai;
    private String ngaySinh;
    private String gioiTinh;
    private String cccd;
    private String ngayVaoLam;
    private Integer idRapChieu;
    private String chucVu;
}
