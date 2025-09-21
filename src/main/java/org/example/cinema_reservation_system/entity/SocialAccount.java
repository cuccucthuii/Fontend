package org.example.cinema_reservation_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tai_khoan_xa_hoi")
public class SocialAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tai_khoan_xa_hoi")
    private Integer idSocial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id")
    private UserAccount userAccount;

    @Column(name = "nha_cung_cap ", nullable = false, length = 20)
    private String provider; // GOOGLE, FACEBOOK

    @Column(name = "id_nguoi_dung_nha_cung_cap", nullable = false, length = 100)
    private String providerUserId;

    @Column(name = "email_nguoi_dung_nha_cung_cap", length = 255)
    private String providerUserEmail;

    @Column(name = "ten_nguoi_dung_nha_cung_cap", length = 255)
    private String providerUserName;

    @Column(name = "anh_dai_dien_nguoi_dung_nha_cung_cap", length = 500)
    private String providerUserPicture;

    @Column(name = "ma_truy_cap", columnDefinition = "TEXT")
    private String accessToken;

    @Column(name = "ma_lay_lai", columnDefinition = "TEXT")
    private String refreshToken;

    @Column(name = "thoi_gian_het_han_ma")
    private LocalDateTime tokenExpiresAt;

    @CreationTimestamp
    @Column(name = "ngay_tao", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "trang_thai_hoat_dong", nullable = false)
    private Boolean isActive = true;

}
