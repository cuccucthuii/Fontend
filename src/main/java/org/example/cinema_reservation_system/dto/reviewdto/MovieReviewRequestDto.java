package org.example.cinema_reservation_system.dto.reviewdto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieReviewRequestDto {
    
    @NotNull(message = "ID phim không được để trống")
    private Integer idPhim;

    @NotNull(message = "Số sao không được để trống")
    @JsonAlias({"soSao"})
    @Min(value = 1, message = "Số sao phải từ 1 đến 5")
    @Max(value = 5, message = "Số sao phải từ 1 đến 5")
    private Integer diemDanhGia;

    @NotBlank(message = "Nội dung đánh giá không được để trống")
    @Size(min = 10, max = 1000, message = "Nội dung đánh giá phải từ 10-1000 ký tự")
    private String noiDungDanhGia;

    @Size(max = 100, message = "Tiêu đề đánh giá không được quá 100 ký tự")
    private String tieuDeDanhGia;

    // Thông tin xem phim
    private Boolean daXemPhim = false;
    private LocalDateTime ngayXemPhim;
    private String rapChieu;
    private String loaiVe; // 2D, 3D, IMAX, VIP
    private Double giaVe;

    // Hình ảnh đánh giá
    private List<MultipartFile> hinhAnhDanhGia;

    // Thông tin bổ sung
    private String loaiHinhAnh; // POSTER, SCREENSHOT, SELFIE, OTHER
    private String moTaHinhAnh;
}

