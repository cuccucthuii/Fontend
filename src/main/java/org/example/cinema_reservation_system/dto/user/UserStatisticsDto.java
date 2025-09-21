package org.example.cinema_reservation_system.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatisticsDto {
    
    // Thống kê cơ bản
    private Integer soVeDaMua; // Số vé đã mua
    private Integer soPhimDaXem; // Số phim đã xem
    private Integer soDanhGia; // Số đánh giá đã viết
    private Integer soBinhLuan; // Số bình luận đã viết
    
    // Thông tin điểm tích lũy
    private Integer diemTichLuy; // Điểm tích lũy hiện tại
    
    // Thống kê đánh giá
    private Double diemTrungBinh; // Điểm đánh giá trung bình
    
    // Phim đã xem gần đây
    private List<RecentMovieDto> phimDaXemGanDay;
    
    // Thông tin bổ sung
    private String capDoNguoiDung; // Cấp độ người dùng (Bronze, Silver, Gold, Platinum)
    private Integer soNgayThamGia; // Số ngày tham gia hệ thống
}
