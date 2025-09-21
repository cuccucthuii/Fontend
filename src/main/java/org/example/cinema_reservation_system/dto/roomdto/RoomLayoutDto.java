package org.example.cinema_reservation_system.dto.roomdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomLayoutDto {
    private Integer idPhong;
    private String tenPhong;
    private Integer chieuRong; // Số cột (columns)
    private Integer chieuDai;  // Số hàng (rows)
    private Double dienTich;   // Diện tích phòng (m²)
    private Integer tongSoGhe;
    private List<SeatPositionDto> danhSachGhe;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeatPositionDto {
        private Integer idGhe;
        private String tenGhe;     // Ví dụ: "A1", "B5"
        private Integer hang;      // Hàng (A=1, B=2, ...)
        private Integer cot;       // Cột (1, 2, 3, ...)
        private String trangThai;  // TRONG, DA_BAN, KHONG_CO
        private String loaiGhe;    // THUONG, VIP, COUPLE
        private BigDecimal giaGhe;
    }
} 