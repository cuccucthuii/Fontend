package org.example.cinema_reservation_system.dto.reviewdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieReviewStatsDto {
    private Integer idPhim;
    private String tenPhim;
    
    // Thống kê tổng quan
    private Integer tongSoDanhGia;
    private Double diemTrungBinh;
    private Integer soDanhGiaCoAnh;
    private Integer soDanhGiaCoPhanHoi;
    
    // Phân bố điểm đánh giá
    private Map<Integer, Integer> phanBoDiem; // 1-5 sao
    
    // Bỏ thống kê chi tiết theo hạng mục để đơn giản hóa: chỉ giữ trung bình chung và phân bố sao
    
    // Thống kê theo thời gian
    private Integer soDanhGiaTrongTuan;
    private Integer soDanhGiaTrongThang;
    private Integer soDanhGiaTrongNam;
    
    // Thống kê theo rạp chiếu
    private Map<String, Integer> danhGiaTheoRap;
    
    // Thống kê theo loại vé
    private Map<String, Integer> danhGiaTheoLoaiVe;
    
    // Đánh giá mới nhất
    private MovieReviewResponseDto danhGiaMoiNhat;
    
    // Đánh giá được thích nhiều nhất
    private MovieReviewResponseDto danhGiaNhieuThichNhat;
}

