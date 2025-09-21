package org.example.cinema_reservation_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "email_history")
public class EmailHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_email")
    private Integer idEmail;

    @ManyToOne
    @JoinColumn(name = "hoa_don_id")
    private Invoice hoaDon;

    @ManyToOne
    @JoinColumn(name = "template_id")
    private EmailTemplate template;

    @Column(name = "email_nguoi_nhan", nullable = false, length = 255)
    private String emailNguoiNhan;

    @Column(name = "tieu_de", nullable = false, length = 200)
    private String tieuDe;

    @Column(name = "noi_dung_html", columnDefinition = "TEXT")
    private String noiDungHtml;

    @Column(name = "noi_dung_text", columnDefinition = "TEXT")
    private String noiDungText;

    @Column(name = "trang_thai", nullable = false, length = 20)
    private String trangThai = "PENDING"; // PENDING, SENT, FAILED

    @Column(name = "loi_gui", length = 500)
    private String loiGui;

    @CreationTimestamp
    @Column(name = "ngay_gui", nullable = false, updatable = false)
    private LocalDateTime ngayGui;

    @Column(name = "ngay_thanh_cong")
    private LocalDateTime ngayThanhCong;

    @Column(name = "so_lan_thu_lai", nullable = false)
    private Integer soLanThuLai = 0;
}

