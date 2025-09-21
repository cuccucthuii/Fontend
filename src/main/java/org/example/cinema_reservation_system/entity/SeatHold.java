package org.example.cinema_reservation_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "giu_ghe")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatHold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_giu_ghe")
    private Integer idGiuGhe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_suat_chieu", nullable = false)
    private ShowTime suatChieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ghe_ngoi", nullable = false)
    private Seat gheNgoi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_account")
    private UserAccount userAccount;

    @Column(name = "hold_token", unique = true)
    private String holdToken;

    @Column(name = "thoi_gian_bat_dau")
    private LocalDateTime thoiGianBatDau;

    @Column(name = "thoi_gian_het_han")
    private LocalDateTime thoiGianHetHan;

    @Column(name = "trang_thai")
    private String trangThai; // DANG_GIU, HET_HAN, DA_MUA, HUY
}




















