package org.example.cinema_reservation_system.dto.userdto;

import lombok.Data;
import org.example.cinema_reservation_system.entity.UserAccount;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserResponseDto {

    private Integer id;
    private String username;
    private String hoTen;
    private String ten;
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

    //thêm
    public static UserResponseDto from(UserAccount u) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(u.getIdTaiKhoan());
        dto.setUsername(u.getTenDangNhap());

        // Map thông tin cá nhân từ bảng liên kết (đổi getter theo entity thật)
        if (u.getKhachHang() != null) {
            var c = u.getKhachHang();
            dto.setHoTen(c.getTenKhachHang());
            dto.setSoDienThoai(c.getSoDienThoaiKhachHang());
            dto.setNgaySinh(c.getNgaySinh());
            dto.setGioiTinh(c.getGioiTinh());
            dto.setDiaChi(c.getDiaChi());
        } else if (u.getNhanVien() != null) {
            var s = u.getNhanVien();
            dto.setHoTen(s.getTenNhanVien());
            dto.setSoDienThoai(s.getSoDienThoaiNhanVien());
            dto.setNgaySinh(s.getNgaySinh());
            dto.setGioiTinh(s.getGioiTinh());
            dto.setDiaChi(s.getDiaChi());
        }

        dto.setEmail(u.getEmail());

        // Role là entity → ưu tiên code, fallback tên
        // Role là entity → ưu tiên tên, fallback id
        String roleStr = null;
        if (u.getVaiTro() != null) {
            var r = u.getVaiTro();
            if (r.getTenVaiTro() != null && !r.getTenVaiTro().isBlank()) {
                roleStr = r.getTenVaiTro();
            } else if (r.getIdRole() != null) {
                roleStr = String.valueOf(r.getIdRole());
            } else {
                roleStr = r.toString();
            }
        }
        dto.setRole(roleStr);

        dto.setTrangThai(u.getTrangThai() == null ? null : u.getTrangThai().name());
        dto.setCreatedAt(u.getNgayTao());
        dto.setUpdatedAt(u.getNgayCapNhat());
        dto.setLastLogin(u.getLastLogin() != null ? u.getLastLogin() : u.getLastLogin());
        return dto;
    }
}
