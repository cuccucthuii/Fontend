package org.example.cinema_reservation_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.cinema_reservation_system.utils.enums.TrangThaiCheckin;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "checkin_ve")
public class TicketCheckin {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_checkin")
    private Integer idCheckin;
    
    @Column(name = "ma_ve", length = 50)
    private String maVe;
    
    @Column(name = "thoi_gian_checkin", nullable = false)
    private LocalDateTime thoiGianCheckin;
    
    @Column(name = "trang_thai", length = 20)
    private String trangThai = "DA_CHECKIN"; // "DA_CHECKIN", "DA_HUY"
    
    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;
    
    @ManyToOne
    @JoinColumn(name = "id_nhan_vien_checkin")
    private Staff nhanVienCheckin;
    
    // Enum đã được di chuyển ra package utils.enums
}


