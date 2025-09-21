package org.example.cinema_reservation_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "thong_bao")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_thong_bao")
    private Integer idThongBao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang")
    private Customer khachHang; // NULL = broadcast

    @Column(name = "tieu_de", nullable = false, length = 200)
    private String tieuDe;

    @Column(name = "noi_dung", nullable = false, columnDefinition = "TEXT")
    private String noiDung;

    @Column(name = "loai")
    private String loai; // DAT_VE, THANH_TOAN, KHUYEN_MAI, HE_THONG

    @Column(name = "kenh")
    private String kenh; // IN_APP, EMAIL, SMS, PUSH

    @Column(name = "trang_thai")
    private String trangThai; // PENDING, SENT, FAILED, READ

    @Column(name = "thoi_gian_gui")
    private LocalDateTime thoiGianGui;

    @Column(name = "thoi_gian_doc")
    private LocalDateTime thoiGianDoc;

    @Column(name = "metadata")
    private String metadata; // map JSONB như String (nếu cần có thể dùng @Type JSON)

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;
}




















