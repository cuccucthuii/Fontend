package org.example.cinema_reservation_system.dto.employee;

import lombok.Data;

import java.time.LocalDate;
@Data
public class EmployeeDto {
    private Long idNhanVien;
    private String tenNhanVien;
    private String email;
    private String soDienThoai;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String cccd;
    private String trangThai;
    private LocalDate ngayVaoLam;
    private Integer idRapChieu;
    private String chucVu;
}
