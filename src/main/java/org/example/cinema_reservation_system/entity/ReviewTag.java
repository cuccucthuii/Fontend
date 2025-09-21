package org.example.cinema_reservation_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_tags")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewTag {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_danh_gia", nullable = false)
    private MovieReview movieReview;
    
    @Column(name = "ten_tag", nullable = false, length = 50)
    private String tenTag; // Tuyệt vời, Hài lòng, Cảm động, Hài hước, Ý nghĩa, etc.
    
    @Column(name = "mau_sac", length = 20)
    private String mauSac; // Màu sắc của tag
    
    @Column(name = "so_luot_su_dung")
    private Integer soLuotSuDung = 0; // Số lần tag này được sử dụng
    
    @CreationTimestamp
    @Column(name = "ngay_tao", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

















































