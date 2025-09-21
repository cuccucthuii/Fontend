package org.example.cinema_reservation_system.dto.reviewdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.cinema_reservation_system.utils.enums.TrangThaiDanhGia;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieReviewResponseDto {
    private Integer idDanhGia;
    private Integer idPhim;
    private String tenPhim;
    private String posterUrl;

    // Thông tin khách hàng
    private Integer idKhachHang;
    private String tenKhachHang;
    private String anhDaiDienKhachHang;
    private String hangThanhVien;

    // Đánh giá (chung theo số sao)
    private Integer diemDanhGia;
    private String noiDungDanhGia;
    private String tieuDeDanhGia;
    private LocalDateTime ngayDanhGia;
    private TrangThaiDanhGia trangThai;

    // Trung bình (theo số sao)
    private Double diemTrungBinh;

    // Thống kê
    private Integer soLuotThich;
    private Integer soLuotPhanHoi;
    private Boolean coAnhKemTheo;

    // Thông tin xem phim
    private Boolean daXemPhim;
    private LocalDateTime ngayXemPhim;
    private String rapChieu;
    private String loaiVe;
    private Double giaVe;

    // Hình ảnh đánh giá
    private List<ReviewImageDto> hinhAnhDanhGia;

    // Phản hồi
    private List<ReviewReplyDto> phanHoi;

    // Thông tin bổ sung
    private Boolean daThich; // Người dùng hiện tại đã thích chưa
    private Boolean coTheChinhSua; // Có thể chỉnh sửa không
    private Boolean coTheXoa; // Có thể xóa không

    // Alias JSON cho Frontend
    @JsonProperty("soSao")
    public Integer getSoSao() {
        return this.diemDanhGia;
    }

    @JsonProperty("soSaoTrungBinh")
    public Double getSoSaoTrungBinh() {
        return this.diemTrungBinh;
    }
}


