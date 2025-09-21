package org.example.cinema_reservation_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_media")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewMedia {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_danh_gia", nullable = false)
    private MovieReview movieReview;
    
    @Column(name = "url_media", nullable = false, length = 500)
    private String mediaUrl;
    
    @Column(name = "loai_media", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private MediaType loaiMedia; // IMAGE, VIDEO
    
    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "thoi_luong_video") // Thời lượng video (giây)
    private Integer thoiLuongVideo;
    
    @Column(name = "kich_thuoc_file") // Kích thước file (bytes)
    private Long kichThuocFile;
    
    @Column(name = "duoi_file", length = 10) // .jpg, .mp4, etc.
    private String duoiFile;
    
    @CreationTimestamp
    @Column(name = "ngay_tao", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    public enum MediaType {
        IMAGE, VIDEO
    }
}

















































