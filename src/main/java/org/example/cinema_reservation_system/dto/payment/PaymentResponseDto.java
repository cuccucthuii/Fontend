package org.example.cinema_reservation_system.dto.payment;

import lombok.Data;
import org.example.cinema_reservation_system.utils.enums.TrangThaiThanhToan;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PaymentResponseDto {
    private Integer idThanhToan;
    private Integer idHoaDon;
    private BigDecimal soTien;
    private String phuongThucThanhToan;
    private TrangThaiThanhToan trangThai;
    private LocalDate ngayThanhToan;
}
