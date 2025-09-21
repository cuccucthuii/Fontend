package org.example.cinema_reservation_system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import org.example.cinema_reservation_system.utils.enums.TrangThaiUserAccount;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tai_khoan_nguoi_dung")
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user_account")
    private Integer idTaiKhoan;

    @Column(name = "ten_dang_nhap", nullable = false, unique = true, length = 100)
    private String tenDangNhap;

    @Column(name = "mat_khau", length = 255)
    private String matKhau; // Có thể null cho social login

    @Column(name = "lan_dang_nhap_cuoi")
    private LocalDateTime lastLogin;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @Column(name = "email", length = 100)
    private String email;

    @ManyToOne
    @JoinColumn(name = "id_vai_tro", nullable = false)
    private Role vaiTro;

    @ManyToOne
    @JoinColumn(name = "id_khach_hang")
    private Customer khachHang;

    @ManyToOne
    @JoinColumn(name = "id_nhan_vien")
    private Staff nhanVien;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private TrangThaiUserAccount trangThai;

    // Social Login fields
    @Column(name = "social_id", length = 100)
    private String socialId;

    @Column(name = "social_provider", length = 20)
    private String socialProvider; // GOOGLE, FACEBOOK

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "email_verified")
    private Boolean emailVerified = false;

    @Column(name = "social_verified")
    private Boolean socialVerified = false;

    // Refresh token field
    @Column(name = "refresh_token", columnDefinition = "TEXT")
    private String refreshToken;

    @Column(name = "refresh_token_expires_at")
    private LocalDateTime refreshTokenExpiresAt;

    // Relationship với SocialAccount
    @OneToMany(mappedBy = "userAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SocialAccount> socialAccount;

    //thêm mới
    @Override
    public String toString() {
        return "UserAccount{id=" + idTaiKhoan + ", username=" + tenDangNhap + "}";
    }

    @Column(name = "da_xoa", nullable = false)
    private Boolean daXoa = false;

}
