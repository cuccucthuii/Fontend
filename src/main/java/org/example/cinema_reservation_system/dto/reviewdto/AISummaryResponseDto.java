package org.example.cinema_reservation_system.dto.reviewdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AISummaryResponseDto {
    private Long id;
    private Integer idPhim;
    private String tenPhim;
    
    // Nội dung tổng hợp
    private String noiDungTongHop;
    
    // Số lượng đánh giá theo hạng mục
    private Integer danhGiaTongThe;
    private Integer danhGiaThongDiep;
    private Integer danhGiaKichBan;
    private Integer danhGiaCamXuc;
    
    // Thống kê feedback
    private Integer soLuotThich;
    private Integer soLuotKhongThich;
    
    // Trạng thái
    private String trangThai;
    private LocalDateTime createdAt;
    private LocalDateTime ngayCapNhat;
    
    // Thông tin bổ sung
    private Boolean daThich; // Người dùng hiện tại đã thích chưa
    private Boolean daKhongThich; // Người dùng hiện tại đã không thích chưa
}

















































