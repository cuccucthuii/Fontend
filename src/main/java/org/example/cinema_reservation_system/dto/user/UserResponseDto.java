package org.example.cinema_reservation_system.dto.user;
import lombok.Data;
import org.example.cinema_reservation_system.entity.Customer;
import org.example.cinema_reservation_system.entity.UserAccount;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserResponseDto {
    private Integer id;
    private String username;

    private String hoTen;
    private String email;
    private String soDienThoai;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String diaChi;

    private String role;
    private String trangThai;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLogin;

    public static UserResponseDto from(UserAccount u) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(u.getIdTaiKhoan());
        dto.setUsername(u.getTenDangNhap());

        Customer kh = u.getKhachHang();
        if (kh != null) {
            dto.setHoTen(kh.getTenKhachHang());
            dto.setSoDienThoai(kh.getSoDienThoaiKhachHang());
            dto.setNgaySinh(kh.getNgaySinh());
            dto.setGioiTinh(kh.getGioiTinh());
            dto.setDiaChi(kh.getDiaChi());
        }
        // các trường thuộc UserAccount
        dto.setEmail(u.getEmail());
        dto.setRole(u.getVaiTro() != null ? u.getVaiTro().getTenVaiTro() : null);
        dto.setTrangThai(u.getTrangThai() != null ? u.getTrangThai().name() : null);
        dto.setCreatedAt(u.getNgayTao());
        dto.setUpdatedAt(u.getNgayCapNhat());
        dto.setLastLogin(u.getLastLogin());
        return dto;
    }
}