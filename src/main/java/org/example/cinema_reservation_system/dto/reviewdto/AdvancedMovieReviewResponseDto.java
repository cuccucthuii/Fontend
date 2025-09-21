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
public class AdvancedMovieReviewResponseDto {
    private Integer idDanhGia;
    private Integer idPhim;
    private String tenPhim;
    private String posterUrl;

    // Thông tin khách hàng
    private Integer idKhachHang;
    private String tenKhachHang;
    private String anhDaiDienKhachHang;
    private String hangThanhVien;
    private Integer tongDiem;
    private List<UserBadgeDto> badges;

    // Đánh giá
    private Integer diemDanhGia;
    private String noiDungDanhGia;
    private String tieuDeDanhGia;
    private LocalDateTime ngayDanhGia;
    private TrangThaiDanhGia trangThai;

    // Thống kê
    private Integer soLuotThich;
    private Integer soLuotPhanHoi;
    private Integer soLuotXem;
    private Boolean coVideo;
    private Integer diemCong;

    // Thông tin xem phim
    private Boolean daXemPhim;
    private LocalDateTime ngayXemPhim;
    private String rapChieu;
    private String loaiVe;
    private Double giaVe;
    private String phuongThucMua;

    // Media đánh giá
    private List<ReviewMediaDto> reviewMedia;

    // Tags đánh giá
    private List<ReviewTagDto> reviewTags;

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
        return this.diemDanhGia != null ? this.diemDanhGia.doubleValue() : 0.0;
    }

    // Inner DTOs
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserBadgeDto {
        private Long id;
        private String tenBadge;
        private String moTa;
        private String iconUrl;
        private String loaiBadge;
        private Integer diemCong;
        private LocalDateTime ngayNhan;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewMediaDto {
        private Long id;
        private String mediaUrl;
        private String loaiMedia; // IMAGE, VIDEO
        private String description;
        private Integer thoiLuongVideo; // Chỉ cho video
        private Long kichThuocFile;
        private String duoiFile;
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewTagDto {
        private Long id;
        private String tenTag;
        private String mauSac;
        private Integer soLuotSuDung;
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewReplyDto {
        private Long id;
        private String noiDungPhanHoi;
        private LocalDateTime ngayPhanHoi;
        private String tenNhanVien;
        private String anhDaiDienNhanVien;
        private TrangThaiDanhGia trangThai;
    }
}

















































