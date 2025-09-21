package org.example.cinema_reservation_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.cinema_reservation_system.utils.enums.TrangThai;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_replies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewReply {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_danh_gia", nullable = false)
    private MovieReview movieReview;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nhan_vien")
    private Employee employee;
    
    @Column(name = "noi_dung_phan_hoi", nullable = false, columnDefinition = "TEXT")
    private String replyContent;
    
    @CreationTimestamp
    @Column(name = "ngay_phan_hoi", nullable = false, updatable = false)
    private LocalDateTime repliedAt;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    private TrangThai status = TrangThai.HOAT_DONG;
}

