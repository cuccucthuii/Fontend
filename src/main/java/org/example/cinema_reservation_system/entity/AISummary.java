package org.example.cinema_reservation_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ai_summaries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AISummary {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phim", nullable = false)
    private Movie phim;
    
    @Column(name = "noi_dung_tong_hop", columnDefinition = "TEXT")
    private String noiDungTongHop; // Nội dung tổng hợp từ AI
    
    @Column(name = "danh_gia_tong_the")
    private Integer danhGiaTongThe; // Số lượng đánh giá tổng thể
    
    @Column(name = "danh_gia_thong_diep")
    private Integer danhGiaThongDiep; // Số lượng đánh giá về thông điệp
    
    @Column(name = "danh_gia_kich_ban")
    private Integer danhGiaKichBan; // Số lượng đánh giá về kịch bản
    
    @Column(name = "danh_gia_cam_xuc")
    private Integer danhGiaCamXuc; // Số lượng đánh giá về cảm xúc
    
    @Column(name = "so_luot_thich")
    private Integer soLuotThich = 0; // Số lượt thích AI summary
    
    @Column(name = "so_luot_khong_thich")
    private Integer soLuotKhongThich = 0; // Số lượt không thích AI summary
    
    @Column(name = "trang_thai", length = 20)
    private String trangThai = "ACTIVE"; // ACTIVE, INACTIVE
    
    @CreationTimestamp
    @Column(name = "ngay_tao", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;
    
    @PreUpdate
    protected void onUpdate() {
        this.ngayCapNhat = LocalDateTime.now();
    }
}

















































