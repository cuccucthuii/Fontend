package org.example.cinema_reservation_system.dto.showtimedto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.cinema_reservation_system.utils.enums.TrangThaiSuatChieu;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShowTimeDetailDto {
    
    // Basic showtime information
    private Integer idSuatChieu;
    private String tenSuatChieu;
    private LocalDate ngayChieu;
    private LocalTime gioChieu;
    private LocalTime thoiGianBatDau;
    private LocalTime thoiGianKetThuc;
    private TrangThaiSuatChieu trangThai;
    private BigDecimal giaVe;
    private Integer soGheConTrong;
    private Integer tongSoGhe;
    private String ghiChu;
    
    // Related entities
    private MovieDto phim;
    private RoomDto phongChieu;
    private TheaterDto rapChieu;
    
    // Additional information
    private List<BookingDto> danhSachDatVe;
    private List<SeatDto> danhSachGhe;
    
    // Statistics
    private Integer soVeDaDat;
    private Integer soVeConTrong;
    private BigDecimal doanhThu;
    private Double tyLeLapDay;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MovieDto {
        private Integer idPhim;
        private String tenPhim;
        private String posterUrl;
        private Integer thoiLuong;
        private String dinhDang;
        private String tuoiGioiHan;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RoomDto {
        private Integer idPhongChieu;
        private String tenPhong;
        private Integer soGhe;
        private String loaiPhong;
        private String trangThietBi;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class TheaterDto {
        private Integer idRap;
        private String tenRap;
        private String diaChi;
        private String soDienThoai;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class BookingDto {
        private Integer idDatVe;
        private String maDatVe;
        private String tenKhachHang;
        private String soDienThoai;
        private Integer soVe;
        private BigDecimal tongTien;
        private String trangThaiDatVe;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SeatDto {
        private Integer idGhe;
        private String tenGhe;
        private String hangGhe;
        private Integer soGhe;
        private String trangThaiGhe;
        private String loaiGhe;
    }
}











































