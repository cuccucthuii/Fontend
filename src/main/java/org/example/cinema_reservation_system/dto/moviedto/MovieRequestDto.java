package org.example.cinema_reservation_system.dto.moviedto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.cinema_reservation_system.utils.enums.TrangThai;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieRequestDto {

    @NotBlank(message = "Tên phim không được để trống")
    @Size(max = 100, message = "Tên phim tối đa 100 ký tự")
    private String tenPhim;

    @NotBlank(message = "Mô tả không được để trống")
    @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
    private String moTa;

    @NotNull(message = "Thời lượng không được để trống")
    @Min(value = 1, message = "Thời lượng phải lớn hơn 0")
    private Integer thoiLuong;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull(message = "Ngày phát hành không được để trống")
    private LocalDate ngayPhatHanh;

    @NotNull(message = "Trạng thái phim không được để trống")
    private TrangThai trangThai;

    @NotBlank(message = "Định dạng phim không được để trống")
    @Pattern(regexp = "2D|3D|4D|IMAX", message = "Định dạng phải là 2D, 3D, 4D hoặc IMAX")
    private String dinhDang;

    @NotBlank(message = "Tuổi giới hạn không được để trống")
    @Pattern(regexp = "P|T13|T16|T18", message = "Tuổi giới hạn phải là P, T13, T16 hoặc T18")
    private String tuoiGioiHan;

    @NotNull(message = "Năm sản xuất không được để trống")
    @Min(value = 2020, message = "Năm sản xuất phải từ 2020")
    @Max(value = 2025, message = "Năm sản xuất phải đến 2025")
    private Integer namSanXuat;

    @Pattern(
            regexp = "^(https?://)?(www\\.)?(youtube\\.com|youtu\\.be)/.+$",
            message = "URL trailer phải là liên kết hợp lệ đến YouTube"
    )
    private String trailerUrl;
    
    // Media URLs (alternative to file upload)
    @Pattern(
            regexp = "^(https?://).+\\.(jpg|jpeg|png|gif|webp)$",
            message = "URL poster phải là liên kết hợp lệ đến hình ảnh"
    )
    private String posterUrl;
    
    @Pattern(
            regexp = "^(https?://).+\\.(jpg|jpeg|png|gif|webp)$",
            message = "URL banner phải là liên kết hợp lệ đến hình ảnh"
    )
    private String bannerUrl;


    // Danh sách ID (có thể để trống nếu cung cấp trường "...Moi")
    private Set<Integer> theLoaiIds;

    private Set<Integer> daoDienIds;

    private Set<Integer> dienVienIds;

    // Tên các thực thể mới muốn thêm
    private Set<String> theLoaiMoi;

    private Set<String> daoDienMoi;

    private Set<String> dienVienMoi;
}