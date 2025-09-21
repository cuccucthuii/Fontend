package org.example.cinema_reservation_system.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.cinema_reservation_system.utils.enums.TrangThaiKhachHang;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseDto {
    
    private Integer idKhachHang;
    private String tenKhachHang;
    private String email;
    private String soDienThoaiKhachHang;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private TrangThaiKhachHang trangThai;
    private Integer diemTichLuy;
    private String cccd;
    private LocalDate ngayTao;
    private String hangThanhVien;
    private String ghiChu;
    private String diaChi;
    private LocalDateTime ngayCapNhat;
    private Integer soLanMuaVe;
    private BigDecimal tongTienDaMua;
    
    // Thông tin avatar
    private Long avatarId;
    private String avatarName;
    private String avatarUrl;
}

