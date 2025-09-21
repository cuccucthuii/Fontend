package org.example.cinema_reservation_system.dto.moviedto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.cinema_reservation_system.entity.*;
import org.example.cinema_reservation_system.utils.enums.TrangThaiPhim;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovieDetailDto {
    
    // Basic movie information
    private Integer idPhim;
    private String tenPhim;
    private String moTa;
    private Integer thoiLuong;
    private LocalDate ngayPhatHanh;
    private TrangThaiPhim trangThai;
    private String dinhDang;
    private LocalDate ngayTao;
    private String tuoiGioiHan;
    private Integer namSanXuat;
    private String doPhoBien;
    private BigDecimal giaVeCoBan;
    private LocalDateTime ngayCapNhat;
    private Integer luotXem;
    private BigDecimal danhGiaTrungBinh;
    
    // Related entities
    private Set<GenreDto> theLoaiList;
    private Set<DirectorDto> daoDienList;
    private Set<ActorDto> dienVienList;
    private TrailerDto trailer;
    private List<ImageDto> hinhAnhs;
    
    // Additional information
    private List<MovieReviewDto> reviews;
    private List<ShowTimeDto> showtimes;
    private List<MovieSummaryDto> similarMovies;
    
    // Statistics
    private Integer totalReviews;
    private Integer totalShowtimes;
    private BigDecimal averageRating;
    private String popularityLevel;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class GenreDto {
        private Integer idTheLoai;
        private String tenTheLoai;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DirectorDto {
        private Integer idDaoDien;
        private String tenDaoDien;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ActorDto {
        private Integer idDienVien;
        private String tenDienVien;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class TrailerDto {
        private Integer idTrailer;
        private String url;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ImageDto {
        private Integer idHinhAnh;
        private String tenHinhAnh;
        private String url;
        private String loaiHinhAnh;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MovieReviewDto {
        private Integer idReview;
        private String noiDung;
        private Integer diemDanhGia;
        private String tenKhachHang;
        private LocalDateTime ngayTao;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ShowTimeDto {
        private Integer idLichChieu;
        private LocalDateTime thoiGianChieu;
        private String tenPhong;
        private String tenRap;
        private BigDecimal giaVe;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MovieSummaryDto {
        private Integer idPhim;
        private String tenPhim;
        private String posterUrl;
        private BigDecimal danhGiaTrungBinh;
        private String trangThai;
    }
}


