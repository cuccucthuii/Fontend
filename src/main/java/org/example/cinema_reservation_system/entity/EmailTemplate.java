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
@Table(name = "email_templates")
public class EmailTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_template")
    private Integer idTemplate;

    @Column(name = "ten_template", nullable = false, unique = true, length = 100)
    private String tenTemplate;

    @Column(name = "tieu_de", nullable = false, length = 200)
    private String tieuDe;

    @Column(name = "noi_dung_html", columnDefinition = "TEXT", nullable = false)
    private String noiDungHtml;

    @Column(name = "noi_dung_text", columnDefinition = "TEXT")
    private String noiDungText;

    @Column(name = "loai_template", nullable = false, length = 50)
    private String loaiTemplate; // BOOKING_CONFIRMATION, PAYMENT_SUCCESS, etc.

    @Column(name = "trang_thai", nullable = false)
    private Boolean trangThai = true;

    @CreationTimestamp
    @Column(name = "ngay_tao", nullable = false, updatable = false)
    private LocalDateTime ngayTao;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat", nullable = false)
    private LocalDateTime ngayCapNhat;
}

