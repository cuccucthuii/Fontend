package org.example.cinema_reservation_system.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PaymentRequestDto {

    @NotNull(message = "ID Hóa Đơn không được trống")
    private Integer idHoaDon;

    @NotBlank(message = "Phương thức thanh toán không được trống")
    private String phuongThucThanhToan;


}
