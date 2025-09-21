package org.example.cinema_reservation_system.dto.emaildto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingConfirmationEmailDto {
    
    // Thông tin giao dịch
    private String maGiaoDich;
    private String maDatVe;
    private LocalDateTime ngayGiaoDich;
    private String phuongThucThanhToan;
    
    // Thông tin phim
    private String tenPhim;
    private LocalDateTime thoiGianChieu;
    private String phongChieu;
    private String rapChieu;
    private String diaChiRap;
    
    // Thông tin vé
    private Integer soVe;
    private List<String> soGhe;
    private String thucAnKem;
    
    // Thông tin khách hàng
    private String tenKhachHang;
    private String emailKhachHang;
    private String soDienThoai;
    
    // Thông tin thanh toán
    private BigDecimal tongTien;
    private String barcodeData;
    
    // Thông tin bổ sung
    private String chinhSachHoanHuy;
    private String lienHeHoTro;
}

















































