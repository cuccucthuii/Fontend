package org.example.cinema_reservation_system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BetaCinemasBookingDTO {
    
    // Thông tin cơ bản
    private Long idDatVe;
    private String maDatVe;
    private Long idKhachHang;
    private String tenKhachHang;
    private String emailKhachHang;
    private String soDienThoaiKhachHang;
    
    // Thông tin lịch chiếu
    private Long idLichChieu;
    private Long idPhim;
    private String tenPhim;
    private String posterPhim;
    private LocalDateTime thoiGianChieu;
    private String ngayChieu;
    private String gioChieu;
    
    // Thông tin rạp và phòng
    private Long idRapChieu;
    private String tenRapChieu;
    private String diaChiRap;
    private Long idPhongChieu;
    private String tenPhongChieu;
    private String loaiPhong;
    
    // Thông tin ghế
    private Set<Long> danhSachIdGhe;
    private List<String> danhSachTenGhe;
    private Integer soLuongVe;
    
    // Thông tin giá và thanh toán
    private BigDecimal giaVe;
    private BigDecimal tongTien;
    private String trangThaiDatVe;
    private String loaiVe;
    private Boolean daThanhToan;
    private String phuongThucThanhToan;
    private String maGiaoDich;
    
    // Thông tin thời gian
    private LocalDateTime ngayDatVe;
    private LocalDateTime ngayCapNhat;
    private LocalDateTime ngayThanhToan;
    private LocalDateTime ngayHuyVe;
    private String lyDoHuy;
    
    // Thông tin check-in
    private Boolean daCheckin;
    private LocalDateTime thoiGianCheckin;
    
    // Thông tin nhân viên
    private Long idNhanVienDatVe;
    private String tenNhanVienDatVe;
    
    // Thông tin bổ sung
    private String ghiChu;
    private Boolean coTheHuyVe = false; // Beta Cinemas không cho phép hủy vé
    private Boolean daHetHan;
    private String thoiGianConLai;
    
    @JsonProperty("chinhSachNoRefund")
    private String chinhSachNoRefund = "Beta Cinemas áp dụng chính sách KHÔNG HOÀN/HỦY VÉ. Vui lòng kiểm tra kỹ thông tin trước khi thanh toán.";
    
    // DTO cho tạo đặt vé mới
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaoDatVeRequest {
        private Long idKhachHang;
        private Long idLichChieu;
        private Long idRapChieu;
        private Long idPhongChieu;
        private Set<Long> danhSachIdGhe;
        private BigDecimal giaVe;
        private String loaiVe;
        private String ghiChu;
        private String phuongThucThanhToan;
    }
    
    // DTO cho cập nhật đặt vé
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CapNhatDatVeRequest {
        private Long idDatVe;
        private Set<Long> danhSachIdGhe;
        private String loaiVe;
        private String ghiChu;
    }
    
    // DTO cho hủy vé
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HuyVeRequest {
        private Long idDatVe;
        private String lyDoHuy;
    }
    
    // DTO cho thanh toán
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ThanhToanRequest {
        private Long idDatVe;
        private String phuongThucThanhToan;
        private String maGiaoDich;
    }
    
    // DTO cho check-in
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckinRequest {
        private Long idDatVe;
        private String maDatVe;
    }
    
    // DTO cho tìm kiếm
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimKiemDatVeRequest {
        private String maDatVe;
        private Long idKhachHang;
        private String emailKhachHang;
        private String soDienThoaiKhachHang;
        private String trangThaiDatVe;
        private LocalDateTime tuNgay;
        private LocalDateTime denNgay;
        private Long idRapChieu;
        private Long idPhim;
        private Boolean daThanhToan;
        private Boolean daCheckin;
    }
    
    // DTO cho thống kê
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ThongKeDatVeResponse {
        private Long tongSoDatVe;
        private Long soDatVeChoThanhToan;
        private Long soDatVeDaThanhToan;
        private Long soDatVeDaCheckin;
        private Long soDatVeDaHuy;
        private BigDecimal tongDoanhThu;
        private BigDecimal doanhThuHomNay;
        private BigDecimal doanhThuTuanNay;
        private BigDecimal doanhThuThangNay;
    }
}



