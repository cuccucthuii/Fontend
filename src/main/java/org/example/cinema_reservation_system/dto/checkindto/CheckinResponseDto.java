package org.example.cinema_reservation_system.dto.checkindto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CheckinResponseDto {
    
    private Integer idCheckin;
    private Integer idVePhim;
    private String maVe;
    private String tenPhim;
    private String tenKhachHang;
    private String tenNhanVienCheckin;
    private LocalDateTime thoiGianCheckin;
    private String trangThai;
    private String ghiChu;
    private String tenPhongChieu;
    private String tenRapChieu;
    private LocalDateTime ngayChieu;
    private String gioChieu;
    private String soGhe;
    private String hangGhe;
}

