package org.example.cinema_reservation_system.dto.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;
@Data
public class AddEmployeeDto {
    @NotBlank(message = "Tên nhân viên không được để trống")
    private String tenNhanVien;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "0\\d{9,10}", message = "Số điện thoại không hợp lệ")
    private String soDienThoai;

    @NotNull(message = "Ngày sinh không được để trống")
    private LocalDate ngaySinh;

    @NotBlank(message = "Giới tính không được để trống")
    @Pattern(regexp = "Nam|Nu|Khac", message = "Giới tính phải là Nam, Nu hoặc Khac")
    private String gioiTinh;

    @NotBlank(message = "CCCD không được để trống")
    private String cccd;

    @NotNull(message = "Ngày vào làm không được để trống")
    private LocalDate ngayVaoLam;

    @NotNull(message = "ID rạp chiếu không được để trống")
    private Integer idRapChieu;
}
