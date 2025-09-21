package org.example.cinema_reservation_system.dto.moviedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.cinema_reservation_system.utils.enums.TrangThai;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieResponseDto {
    private Integer idPhim;

    private String tenPhim;

    private String moTa;

    private Integer thoiLuong;

    private LocalDate ngayPhatHanh;

    private TrangThai trangThai;

    private String dinhDang;

    private LocalDate ngayTao;

    // Bổ sung các trường đang thiếu so với DB
    private String tuoiGioiHan;   // P, T13, T16, T18
    private Integer namSanXuat;   // ví dụ 2025
    private String doPhoBien;     // PHO_BIEN, IT_PHO_BIEN, DAC_BIET
    private java.math.BigDecimal giaVeCoBan; // giá vé cơ bản

    private String posterUrl;

    private String bannerUrl;

    private String trailerUrl;


    private List<String> daoDien;
    private List<String> dienVien;
    private List<String> theLoai;

}
