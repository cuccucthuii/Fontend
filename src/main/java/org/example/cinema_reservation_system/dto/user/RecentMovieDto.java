package org.example.cinema_reservation_system.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecentMovieDto {
    private Integer idPhim;
    private String tenPhim;
    private String posterUrl;
    private LocalDate ngayPhatHanh;
    private LocalDateTime ngayXemPhim;
    private String rapChieu;
    private String loaiVe;
    private Integer soSao; // Đánh giá của user cho phim này
    private String noiDungDanhGia;
}

















































