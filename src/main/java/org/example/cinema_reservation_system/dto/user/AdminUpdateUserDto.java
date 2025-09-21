package org.example.cinema_reservation_system.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.example.cinema_reservation_system.utils.enums.TrangThaiUserAccount;

import java.time.LocalDate;

@Data
public class AdminUpdateUserDto {
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String diaChi;
    private String gioiTinh;         // "Nam" / "Nữ" / "Khác"
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngaySinh;      // FE gửi yyyy-MM-dd
    private String vaiTro;           // "Khách hàng" / "Nhân viên" / "Quản lý"
    private TrangThaiUserAccount trangThai;
}
